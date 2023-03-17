package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Singleton
@Slf4j
public class AddReminderCommand implements SlashCommandHandler {

    @Inject ReminderEntryController reminderEntryController;

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
                                OptionType.NUMBER,
                                "reminder-offset",
                                "how much earlier the user want to be reminded of some event",
                                false),
                        new OptionData(
                                OptionType.NUMBER,
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

        reminderEntryController.addReminder(
                discordUserId, title, reminderTimeString, offset, interval, unitString);

        event.reply("Event Added!").queue();
    }
}
