package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.exception.ReminderNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class AddReminderCommand implements SlashCommandHandler, ButtonHandler {

    @Inject ReminderEntryController reminderEntryController;

    public static MessageEmbed buildEmbed(List<String[]> fields) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        for (String[] field : fields) {
            embedBuilder.addField(field[0], field[1], false);
        }

        return embedBuilder.build();
    }

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
        String discordUserId = event.getUser().getId();
        String title = Objects.requireNonNull(event.getOption("title")).getAsString();
        String reminderTimeString =
                Objects.requireNonNull(event.getOption("reminder-time")).getAsString();

        OptionMapping offsetOption = event.getOption("reminder-offset");
        OptionMapping intervalOption = event.getOption("repeat-interval");
        OptionMapping unitOption = event.getOption("interval-unit");

        Integer offset = offsetOption == null ? 10 : offsetOption.getAsInt();
        Integer interval = intervalOption == null ? null : intervalOption.getAsInt();
        String unitString = unitOption == null ? null : unitOption.getAsString();

        LocalTime reminderTime = null;
        try {
            reminderTime = ReminderEntryController.parseReminderTime(reminderTimeString);
        } catch (DateTimeParseException e) {
            event.reply("Please specify reminder time like 'hh:mm' (24-hour clock format)").queue();
            return;
        }

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
        reminderEntryController.addReminder(
                discordUserId, title, reminderTime, offset, interval, unit);

        List<MessageEmbed> embeds = new ArrayList<>();
        List<String[]> reminderInfo = new ArrayList<>();
        reminderInfo.add(new String[] {"Title", title});
        reminderInfo.add(new String[] {"Reminder Time", reminderTimeString});
        reminderInfo.add(new String[] {"Reminder Offset", String.valueOf(offset)});

        if (interval != null) {
            reminderInfo.add(new String[] {"Repeat Interval", String.valueOf(interval)});
            reminderInfo.add(new String[] {"Repeat Interval Time Unit", unitString});
        }
        MessageEmbed embed = buildEmbed(reminderInfo);
        embeds.add(embed);

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder
                        .addEmbeds(embeds)
                        .addActionRow(
                                Button.primary(
                                        this.getName() + ":comfirm@" + discordUserId, "Confirm"),
                                Button.secondary(
                                        this.getName() + ":cancel@" + discordUserId, "Cancel"));
        messageCreateBuilder =
                messageCreateBuilder.setContent(
                        "Are you sure you would like to add the following reminder?");
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

        String label = event.getButton().getLabel();

        String id = event.getButton().getId();
        Objects.requireNonNull(id);
        String userId = id.split("@", 2)[1];
        if (label.equals("Cancel")) {
            reminderEntryController.cancelReminder(userId);
            event.reply("Request canceled!").queue();
            return;
        }
        try {
            reminderEntryController.confirmReminder(userId);
        } catch (ReminderNotFoundException e) {
            event.reply("Oops, looks like we lost your reminder! Please try adding it again")
                    .queue();
        }
        event.reply("Reminder Added!").queue();
    }
}
