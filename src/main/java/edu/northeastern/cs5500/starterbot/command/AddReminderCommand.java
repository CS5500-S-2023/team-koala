package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import java.time.Duration;
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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class AddReminderCommand implements SlashCommandHandler {

    @Inject ReminderEntryController reminderEntryController;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);

    @Inject
    public AddReminderCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "add-reminder";
    }

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

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /add-reminder");

        // get reminder input
        String discordUserId = event.getUser().getId();
        String title = Objects.requireNonNull(event.getOption("title")).getAsString();
        String reminderTimeString =
                Objects.requireNonNull(event.getOption("reminder-time")).getAsString();

        OptionMapping offsetOption = event.getOption("reminder-offset");
        OptionMapping intervalOption = event.getOption("repeat-interval");
        OptionMapping unitOption = event.getOption("interval-unit");

        // null check on nullable inputs
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

        // add reminder to database
        ReminderEntry savedEntry =
                reminderEntryController.addReminder(
                        discordUserId, title, reminderTime, offset, interval, unit);
        scheduleMessage(savedEntry, event);

        // return reminder info in confirmation message to user
        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.addField("Title", title, false);
        embedBuilder.addField("Reminder Time", reminderTimeString, false);
        embedBuilder.addField("Reminder Offset", String.valueOf(offset), false);
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
        LocalTime reminderTimeActual = entry.getReminderTime().minusMinutes(offset);
        Integer repeatInterval = entry.getRepeatInterval();
        TimeUnit unit = entry.getRepeatTimeUnit();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime nextReminder =
                getNextReminderTime(reminderTimeActual, unit, repeatInterval, now);
        Duration durationTilNextReminder = Duration.between(now, nextReminder);

        long initialDelay = durationTilNextReminder.getSeconds();
        Runnable task =
                new Runnable() {
                    @Override
                    public void run() {
                        ReminderEntry retrivedEntry =
                                reminderEntryController.getReminder(reminderId);
                        if (entry == null) {
                            return;
                        }
                        String message =
                                String.format(
                                        "Hello <@%s>! You have %s coming up in %d minutes, get ready!",
                                        retrivedEntry.getDiscordUserId(),
                                        retrivedEntry.getTitle(),
                                        retrivedEntry.getReminderOffset());
                        JDA jda = event.getJDA();
                        for (Guild guild : jda.getGuilds()) {
                            guild.getDefaultChannel().asTextChannel().sendMessage(message).queue();
                        }
                        if (entry.getRepeatInterval() == null) {
                            reminderEntryController.deleteReminder(reminderId);
                        }
                    }
                };
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

    // Reminder messages start the next time the clock hits reminderTime
    // e.g. now is 10am the user scheduled a reminder at 8am then the first reminder
    // message will be at 8am the next day
    public ZonedDateTime getNextReminderTime(
            LocalTime reminderTimeActual,
            TimeUnit unit,
            Integer repeatInterval,
            ZonedDateTime now) {
        ZonedDateTime nextReminder =
                now.withHour(reminderTimeActual.getHour())
                        .withMinute(reminderTimeActual.getMinute());
        if (repeatInterval == null) {
            repeatInterval = 1;
            unit = TimeUnit.DAYS;
        }

        int today = now.getDayOfMonth();
        while (now.compareTo(nextReminder) >= 0 && nextReminder.getDayOfMonth() == today) {
            switch (unit) {
                case MINUTES:
                    nextReminder = nextReminder.plusMinutes(repeatInterval);
                    break;
                case HOURS:
                    nextReminder = nextReminder.plusHours(repeatInterval);
                    break;
                case DAYS:
                    nextReminder = nextReminder.plusDays(repeatInterval);
                    break;
                default:
                    break;
            }
        }
        return nextReminder;
    }
}
