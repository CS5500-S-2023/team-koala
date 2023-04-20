package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.UnableToAddReminderException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.service.ReminderMessageTask;
import edu.northeastern.cs5500.starterbot.service.ReminderSchedulingService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * The command that allows users to add a reminder by specifying title, reminder time, offset repeat
 * interval (optional), and reminder time unit (optional)
 */
@Singleton
@Slf4j
public class AddReminderCommand implements SlashCommandHandler {
    @Inject ReminderEntryController reminderEntryController;
    @Inject ReminderSchedulingService reminderSchedulingService;
    @Inject JDA jda;

    @Inject
    public AddReminderCommand() {}

    /**
     * Returns the name of the command.
     *
     * @return String - the name of the command.
     */
    @Override
    @Nonnull
    public String getName() {
        return "add-reminder";
    }

    /**
     * Returns the name and options of this command.
     *
     * @return CommandData - information about this command
     */
    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Tell the bot about the reminder you want to add")
                .addOptions(
                        new OptionData(OptionType.STRING, "title", "Title of the event")
                                .setRequired(true),
                        new OptionData(
                                OptionType.STRING,
                                "reminder-time",
                                "when the reminded event start",
                                true),
                        new OptionData(
                                OptionType.INTEGER,
                                "reminder-offset",
                                "how much earlier do you want us to remind you",
                                false),
                        new OptionData(
                                OptionType.INTEGER,
                                "delay",
                                "how many days later do you expect the first reminder",
                                false),
                        new OptionData(
                                OptionType.INTEGER,
                                "repeat-interval",
                                "Interval between 2 reminder messages if reminder is repeated",
                                false),
                        new OptionData(
                                        OptionType.STRING,
                                        "interval-unit",
                                        "Time unit of the repeat interval",
                                        false)
                                .addChoice("minute", "m")
                                .addChoice("hour", "h")
                                .addChoice("day", "d"));
    }

    /**
     * When user interacts with this command ('event' happens) The command checks the input while
     * parsing the data, Stores the reminder info into database, And schedules reminder messages
     * based on reminder info.
     *
     * @param event - user's interaction event
     */
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /add-reminder");

        // get reminder input
        String discordUserId = event.getUser().getId();
        String title = Objects.requireNonNull(event.getOption("title")).getAsString();
        String reminderTimeString =
                Objects.requireNonNull(event.getOption("reminder-time")).getAsString();

        OptionMapping delayOption = event.getOption("delay");
        OptionMapping offsetOption = event.getOption("reminder-offset");
        OptionMapping intervalOption = event.getOption("repeat-interval");
        OptionMapping unitOption = event.getOption("interval-unit");

        // null check on nullable inputs
        Integer delay = delayOption == null ? 0 : delayOption.getAsInt();
        Integer offset = offsetOption == null ? 10 : offsetOption.getAsInt();
        Integer interval = intervalOption == null ? null : intervalOption.getAsInt();
        String unitString = unitOption == null ? null : unitOption.getAsString();

        // parse reminder time
        LocalTime reminderTime = null;
        try {
            reminderTime = LocalTime.parse(reminderTimeString);
        } catch (DateTimeParseException e) {
            event.reply("Please specify reminder time like 'hh:mm' (24-hour clock format)").queue();
            return;
        }

        // parse reminder time unit
        TimeUnit unit = interval != null ? ReminderEntryController.parseTimeUnit(unitString) : null;

        // calculate actual reminder message time and the time of the first reminder message
        reminderTime = reminderTime.minusMinutes(offset);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ReminderSchedulingService.TIME_ZONE));
        ZonedDateTime firstReminderTimeZoned =
                getFirstReminderTime(reminderTime, delay, unit, interval, now);
        LocalDateTime firstReminderTime = firstReminderTimeZoned.toLocalDateTime();

        // add reminder to database
        ReminderEntry savedEntry =
                ReminderEntry.builder()
                        .discordUserId(discordUserId)
                        .title(title)
                        .reminderTime(reminderTime)
                        .nextReminderTime(firstReminderTime)
                        .reminderOffset(offset)
                        .repeatInterval(interval)
                        .repeatTimeUnit(unit)
                        .build();
        try {
            savedEntry =
                    reminderEntryController.addReminder(
                            discordUserId,
                            title,
                            reminderTime,
                            firstReminderTime,
                            offset,
                            interval,
                            unit);
            if (savedEntry == null) {
                throw new UnableToAddReminderException("Null returned when persisting reminder");
            }
            scheduleMessage(savedEntry);
        } catch (UnableToAddReminderException utare) {
            event.reply("Sorry! Something went wrong when saving your reminder, please try again.")
                    .queue();
            log.error(
                    "Unable to preserve reminder for user {}.\n Details - {}",
                    discordUserId,
                    savedEntry);
        } catch (RejectedExecutionException ree) {
            log.error(
                    "Unable to schedule reminder for user {}.\n Details - {}",
                    discordUserId,
                    savedEntry);
        }

        // return reminder info in confirmation message to user
        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("Title", title, false);
        embedBuilder.addField("Reminder Time", reminderTimeString, false);
        embedBuilder.addField("Reminder Offset", String.valueOf(offset), false);
        embedBuilder.addField("Delay", String.valueOf(delay), false);
        if (interval != null) {
            embedBuilder.addField("Repeat Interval", String.valueOf(interval), false);
            embedBuilder.addField("Repeat Interval Time Unit", unitString, false);
        }
        MessageEmbed embed = embedBuilder.build();
        embeds.add(embed);

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embeds);
        messageCreateBuilder =
                messageCreateBuilder.setContent(
                        "The following reminder has been successfully added!");
        event.reply(messageCreateBuilder.build()).queue();
    }

    /**
     * Schedule the reminder message to start at the nextReminderTime specified in entry
     *
     * @param entry - the entry to schedule reminder messages for.
     */
    private void scheduleMessage(ReminderEntry entry) throws RejectedExecutionException {
        String reminderId = entry.getId().toString();
        Integer repeatInterval = entry.getRepeatInterval();
        TimeUnit unit = entry.getRepeatTimeUnit();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ReminderSchedulingService.TIME_ZONE));

        ZonedDateTime nextReminder =
                entry.getNextReminderTime().atZone(ZoneId.of(ReminderSchedulingService.TIME_ZONE));

        Duration durationTilNextReminder = Duration.between(now, nextReminder);

        long initialDelay = durationTilNextReminder.getSeconds();
        ReminderSchedulingService.scheduleTask(
                new ReminderMessageTask(reminderId, reminderEntryController, jda),
                initialDelay,
                unit,
                repeatInterval);
    }

    /**
     * Reminder messages start the next time the clock hits reminderTime e.g. now is 10am the user
     * scheduled a reminder at 8am then the first reminder message will be at 8am the next day
     *
     * @param reminderTimeActual - the actual message time = reminderTime - offset
     * @param delay - the initial delay of the reminder in days
     * @param unit - TimeUnit of repeating reminders
     * @param repeatInterval - interval between two reminder messages if the reminder repeats
     * @param now - time of execution
     * @return ZonedDateTime - the time of the next reminder message
     */
    private ZonedDateTime getFirstReminderTime(
            LocalTime reminderTimeActual,
            Integer delay,
            TimeUnit unit,
            Integer repeatInterval,
            ZonedDateTime now) {
        ZonedDateTime nextReminder =
                now.withHour(reminderTimeActual.getHour())
                        .withMinute(reminderTimeActual.getMinute());
        if (delay > 0) {
            nextReminder = nextReminder.plusDays(delay);
        }
        return ReminderSchedulingService.getNextReminderTime(
                nextReminder, unit, repeatInterval, now);
    }
}
