package edu.northeastern.cs5500.starterbot.command.reminders;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.reminders.UnableToAddReminderException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.service.reminders.ReminderMessageTask;
import edu.northeastern.cs5500.starterbot.service.reminders.ReminderSchedulingService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
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
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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

    protected static final Choice[] TIME_ZONE_CHOICES;

    static {
        TIME_ZONE_CHOICES = new Choice[24];
        for (int i = 0; i < 24; i++) {
            String zone =
                    i >= 12 ? String.format("GMT+%d", i - 12) : String.format("GMT%d", i - 12);
            String zoneId = TimeZone.getTimeZone(zone).getID();
            TIME_ZONE_CHOICES[i] = new Choice(zone, zoneId);
        }
    }

    @Inject
    public AddReminderCommand() {
        // empty as everything is injected
    }

    /**
     * Returns the name of the command.
     *
     * @return String - the name of the command.
     */
    @Override
    @Nonnull
    public String getName() {
        return "add_reminder";
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
                        new OptionData(
                                OptionType.STRING,
                                "title",
                                "title of the event to be reminded of",
                                true),
                        new OptionData(
                                OptionType.STRING,
                                "reminder-time",
                                "when the reminded event start (hh:mm 24 hour clock format, 2 digit for hour and minute each)",
                                true),
                        new OptionData(
                                OptionType.INTEGER,
                                "reminder-offset",
                                "how much earlier do you want us to remind you (in minutes, default: 0)",
                                false),
                        new OptionData(
                                        OptionType.STRING,
                                        "time-zone",
                                        "let us know if you are not in PDT :)",
                                        false)
                                .addChoices(TIME_ZONE_CHOICES),
                        new OptionData(
                                OptionType.INTEGER,
                                "delay",
                                "how many days later do you expect the first reminder (default: 0)",
                                false),
                        new OptionData(
                                        OptionType.STRING,
                                        "interval-unit",
                                        "time unit of the repeat interval (no value by default = reminder does not repeat)",
                                        false)
                                .addChoice("minute", "m")
                                .addChoice("hour", "h")
                                .addChoice("day", "d"),
                        new OptionData(
                                OptionType.INTEGER,
                                "repeat-interval",
                                "interval between 2 reminder messages (no value by default = reminder does not repeat)",
                                false));
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
        OptionMapping timeZoneOption = event.getOption("time-zone");

        // null check on nullable inputs
        Integer delay = delayOption == null ? 0 : delayOption.getAsInt();
        Integer offset = offsetOption == null ? 0 : offsetOption.getAsInt();
        Integer interval = intervalOption == null ? null : intervalOption.getAsInt();
        String unitString = unitOption == null ? null : unitOption.getAsString();
        String timeZone =
                (timeZoneOption == null
                        ? TIME_ZONE_CHOICES[5].getAsString()
                        : timeZoneOption.getAsString());

        // check for negative values
        if (delay < 0 || offset < 0 || interval != null && interval < 0) {
            event.reply(
                            "Please specify delay / reminder-offset / repeat-interval with a non-negative integer.")
                    .queue();
            return;
        }

        // parse reminder time
        LocalTime reminderTime = null;
        try {
            reminderTime = LocalTime.parse(reminderTimeString);
        } catch (DateTimeParseException e) {
            event.reply("Please specify reminder time like 'hh:mm' (24-hour clock format)").queue();
            return;
        }

        // interval and unit should be null / non-null at the same time
        if (unitString == null && interval != null || interval == null && unitString != null) {
            event.reply("Please specify both the repeat interval and unit to repeat the reminder")
                    .queue();
            return;
        }

        // parse reminder time unit
        TimeUnit unit =
                unitString == null ? null : ReminderEntryController.parseTimeUnit(unitString);

        // calculate actual reminder message time and the time of the first reminder message
        reminderTime = reminderTime.minusMinutes(offset);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));
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
                        .timeZone(timeZone)
                        .repeatInterval(interval)
                        .repeatTimeUnit(unit)
                        .build();
        try {
            savedEntry = reminderEntryController.addReminder(savedEntry);
            scheduleMessage(savedEntry);
        } catch (UnableToAddReminderException | RejectedExecutionException e) {
            if (savedEntry.getId() != null) {
                reminderEntryController.deleteReminder(savedEntry.getId().toString());
            }
            event.reply("Sorry! Something went wrong when saving your reminder, please try again.")
                    .queue();
            log.error(
                    "Unable to persist reminder for user {}.\n Details - discordUserId: {}, title: {}, reminderTime: {}, offset: {}, interval: {}, unit: {}",
                    discordUserId,
                    title,
                    reminderTime,
                    offset,
                    interval,
                    unit);
        }

        // return reminder info in confirmation message to user
        MessageCreateData messageData =
                buildReminderReceiptMessage(
                        title, reminderTimeString, offset, timeZone, delay, interval, unit);
        event.reply(messageData).queue();
    }

    /**
     * Build the message with an embed showing the information of the reminder added.
     *
     * @param title - title of the reminder.
     * @param reminderTimeString - reminder time in string format.
     * @param offset - reminder offset.
     * @param delay - initial delay of the reminder.
     * @param interval - repeat interval of the reminder.
     * @param unitString - repeat interval time unit of the reminder.
     * @return MessageCreateData - the message to be send back to user.
     */
    @VisibleForTesting
    MessageCreateData buildReminderReceiptMessage(
            String title,
            String reminderTimeString,
            Integer offset,
            String timeZone,
            Integer delay,
            Integer interval,
            TimeUnit unit) {
        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("Title", title, false);
        embedBuilder.addField("Reminder Time", reminderTimeString, false);
        embedBuilder.addField(
                "Reminder Offset", String.format("%s minute(s)", String.valueOf(offset)), false);
        embedBuilder.addField("Time Zone", timeZone, false);
        embedBuilder.addField("Delay", String.format("%s day(s)", String.valueOf(delay)), false);
        if (interval != null) {
            embedBuilder.addField("Repeat Interval", String.valueOf(interval), false);
            embedBuilder.addField("Repeat Interval Time Unit", String.valueOf(unit), false);
        }
        MessageEmbed embed = embedBuilder.build();
        embeds.add(embed);
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embeds);
        messageCreateBuilder =
                messageCreateBuilder.setContent(
                        "The following reminder has been successfully added!");
        return messageCreateBuilder.build();
    }

    /**
     * Schedule the reminder message to start at the nextReminderTime specified in entry
     *
     * @param entry - the entry to schedule reminder messages for.
     * @throws RejectedExecutionException - when ScheduledExecutorService is not able to schedule
     *     the message.
     */
    private void scheduleMessage(ReminderEntry entry) throws RejectedExecutionException {
        String reminderId = entry.getId().toString();
        Integer repeatInterval = entry.getRepeatInterval();
        TimeUnit unit = entry.getRepeatTimeUnit();
        String timeZone = entry.getTimeZone();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));

        ZonedDateTime nextReminder = entry.getNextReminderTime().atZone(ZoneId.of(timeZone));

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
    @VisibleForTesting
    ZonedDateTime getFirstReminderTime(
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
