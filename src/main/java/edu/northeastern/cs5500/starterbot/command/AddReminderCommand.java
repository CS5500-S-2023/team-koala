package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.Bot;
import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;

/**
 * The command that allows users to add a reminder by specifying title, reminder time, offset repeat
 * interval (optional), and reminder time unit (optional)
 */
@Singleton
@Slf4j
public class AddReminderCommand implements SlashCommandHandler {

    @Inject ReminderEntryController reminderEntryController;
    @Inject ReminderSchedulingService reminderSchedulingService;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);

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
                                "Start time of the event to be reminded of",
                                true),
                        new OptionData(
                                OptionType.INTEGER,
                                "delay",
                                "how many days later the first reminder should start",
                                false),
                        new OptionData(
                                OptionType.INTEGER,
                                "reminder-offset",
                                "how much earlier the user want to be reminded of some event",
                                false),
                        new OptionData(
                                OptionType.INTEGER,
                                "repeat-interval",
                                "Interval between 2 reminders if user wants repeated reminders",
                                false),
                        new OptionData(
                                OptionType.STRING,
                                "interval-unit",
                                "Time unit of the repeat interval",
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
        TimeUnit unit = null;
        if (interval != null && unitString != null) {
            try {
                unit = ReminderEntryController.parseTimeUnit(unitString);
            } catch (InvalidTimeUnitException e) {
                event.reply("Please specify time unit with either m (minute), h (hour) or d (day)")
                        .queue();
                return;
            }
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ReminderSchedulingService.TIME_ZONE));
        ZonedDateTime firstReminderTimeZoned =
                getFirstReminderTime(reminderTime, delay, unit, interval, now);
        LocalDateTime firstReminderTime = firstReminderTimeZoned.toLocalDateTime();
        // add reminder to database
        ReminderEntry savedEntry =
                reminderEntryController.addReminder(
                        discordUserId,
                        title,
                        reminderTime,
                        firstReminderTime,
                        offset,
                        interval,
                        unit);
        scheduleMessage(savedEntry, event);

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

    private void scheduleMessage(ReminderEntry entry, SlashCommandInteractionEvent event) {
        String reminderId = entry.getId().toString();
        Integer offset = entry.getReminderOffset();
        // LocalTime reminderTimeActual = entry.getReminderTime().minusMinutes(offset);
        LocalDateTime firstReminderTimeLocal = entry.getFirstReminderTime();
        ZonedDateTime firstReminderTime =
                firstReminderTimeLocal.atZone(ZoneId.of(ReminderSchedulingService.TIME_ZONE));
        Integer repeatInterval = entry.getRepeatInterval();
        TimeUnit unit = entry.getRepeatTimeUnit();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        // ZonedDateTime reminderTimeActualDateTime =
        // now.withHour(reminderTimeActual.getHour()).withMinute(reminderTimeActual.getMinute());
        // ZonedDateTime nextReminder =
        //         getNextReminderTime(reminderTimeActualDateTime, unit, repeatInterval, now);
        ZonedDateTime nextReminder =
                reminderSchedulingService.getNextReminderTime(
                        firstReminderTime, unit, repeatInterval, now);
        System.out.println(nextReminder);
        Duration durationTilNextReminder = Duration.between(now, nextReminder);

        long initialDelay = durationTilNextReminder.getSeconds();
        reminderSchedulingService.scheduleReminder(
                new ReminderMessageTask(reminderId), initialDelay, unit, repeatInterval);
        Runnable task =
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("loading reminder: " + reminderId);
                        // load the reminder from database
                        ReminderEntry retrivedEntry = reminderEntryController.getReminder(reminderId);

                        // If the reminder is not there any more we don't do anything
                        if (retrivedEntry == null) {
                        System.out.println("null: returning...");
                        return;
                        }

                        System.out.println("Before message");
                        // Send the message
                        String message =
                                String.format(
                                        "Hello <@%s>! You have %s coming up in %d minutes, get ready!",
                                        retrivedEntry.getDiscordUserId(),
                                        retrivedEntry.getTitle(),
                                        retrivedEntry.getReminderOffset());

                        System.out.println(message);
                        JDA jda = Bot.getJDA();
                        String userId = retrivedEntry.getDiscordUserId();
                        for (Guild guild : jda.getGuilds()) {
                            guild.getDefaultChannel().asTextChannel().sendMessage(message).queue();
                        }
                        User user = jda.getUserById(userId);
                        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());

                        // Delete reminder if it's one time
                        if (retrivedEntry.getRepeatInterval() == null) {
                        reminderEntryController.deleteReminder(reminderId);
                        }
                    }
                };
        // Schdule the message(s)
        if (repeatInterval == null) {
            scheduler.schedule(task, initialDelay, TimeUnit.SECONDS);
            return;
        }
        scheduler.scheduleAtFixedRate(
                task,
                initialDelay,
                unit.toSeconds(1) * entry.getRepeatInterval(),
                TimeUnit.SECONDS);
    }

    /**
     * Reminder messages start the next time the clock hits reminderTime e.g. now is 10am the user
     * scheduled a reminder at 8am then the first reminder message will be at 8am the next day
     *
     * @param reminderTimeActual - the actual message time = reminderTime - offset
     * @param unit - TimeUnit of repeating reminders
     * @param repeatInterval - interval between two reminder messages if the reminder repeats
     * @param now - time of execution
     * @return ZonedDateTime - the time of the next reminder message
     */
    public ZonedDateTime getFirstReminderTime(
            LocalTime reminderTimeActual,
            Integer delay,
            TimeUnit unit,
            Integer repeatInterval,
            ZonedDateTime now) {
        ZonedDateTime nextReminder =
                now.withHour(reminderTimeActual.getHour())
                        .withMinute(reminderTimeActual.getMinute());
        if (delay > 0) {
            return nextReminder.plusDays(delay);
        }
        return reminderSchedulingService.getNextReminderTime(
                nextReminder, unit, repeatInterval, now);
    }
}
