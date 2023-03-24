package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    Map<String, ReminderEntry> tempStore = new HashMap<>();

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

        String[] reminderHourMin = reminderTimeString.split(":");
        if (reminderHourMin.length != 2) {
            event.reply("Please specify reminder time like 'hh:mm' (24-hour clock format)").queue();
            return;
        }

        Integer hour = null;
        Integer min = null;
        try {
            hour = Integer.parseInt(reminderHourMin[0]);
            min = Integer.parseInt(reminderHourMin[1]);

            if (hour < 0 || hour >= 24) {
                throw new NumberFormatException("Hour must be an integer from 0 to 23");
            }
            if (min < 0 || min >= 60) {
                throw new NumberFormatException("Minute must be an integer from 0 to 59");
            }
        } catch (NumberFormatException e) {
            event.reply("Please specify reminder time hour and minute with valid numbers").queue();
            return;
        }

        LocalTime reminderTime = LocalTime.of(hour, min);

        TimeUnit unit = null;
        if (interval != null && unitString != null) {
            switch (unitString) {
                case "m":
                    unit = TimeUnit.MINUTES;
                    break;

                case "h":
                    unit = TimeUnit.HOURS;
                    break;

                case "d":
                    unit = TimeUnit.DAYS;
                    break;

                default:
                    event.reply("Please specify repeat interval with m(minute) / h(hour) / d(day)")
                            .queue();
                    return;
            }
        }
        ReminderEntry reminderEntry =
                ReminderEntry.builder()
                        .discordUserId(discordUserId)
                        .title(title)
                        .reminderTime(reminderTime)
                        .reminderOffset(offset)
                        .repeatInterval(interval)
                        .repeatTimeUnit(unit)
                        .build();
        tempStore.put(discordUserId, reminderEntry);

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
                        "Are you sure you would like to add the following event?");
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

        String label = event.getButton().getLabel();

        String id = event.getButton().getId();
        Objects.requireNonNull(id);
        String userId = id.split("@", 2)[1];

        ReminderEntry entry = tempStore.get(userId);
        tempStore.remove(userId);
        if (label.equals("Cancel")) {
            event.reply("Request canceled!").queue();
            return;
        }
        reminderEntryController.addReminder(entry);
        event.reply("Reminder Added!").queue();
    }
}
