package edu.northeastern.cs5500.starterbot.command.reminder_commands;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.Test;

class AddReminderCommandTest {

    static final List<OptionData> OPTIONS =
            Arrays.asList(
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
                                    "time zone of your reminder",
                                    false)
                            .addChoices(AddReminderCommand.TIME_ZONE_CHOICES),
                    new OptionData(
                            OptionType.INTEGER,
                            "delay",
                            "how many days later do you expect the first reminder (default: 0)",
                            false),
                    new OptionData(
                            OptionType.STRING,
                            "interval-unit",
                            "time unit of the repeat interval (defaults to null = reminder does not repeat)",
                            false),
                    new OptionData(
                            OptionType.INTEGER,
                            "repeat-interval",
                            "interval between 2 reminder messages (defaults to null = reminder does not repeat)",
                            false));
    static final String TITLE = "reminder";
    static final String REMINDER_TIME = "14:00";
    static final Integer OFFSET = 5;
    static final Integer INTERVAL = 10;
    static final Integer DELAY = 1;
    static final String UNIT = "h";

    @Test
    void testNameMatchesData() {
        AddReminderCommand addReminderCommand = new AddReminderCommand();
        String name = addReminderCommand.getName();
        SlashCommandData commandData = (SlashCommandData) addReminderCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
        List<OptionData> options = commandData.getOptions();
        assertThat(options.size()).isEqualTo(7);

        for (int i = 0; i < options.size(); i++) {
            assertThat(options.get(i).getName()).isEqualTo(OPTIONS.get(i).getName());
            assertThat(options.get(i).getType()).isEqualTo(OPTIONS.get(i).getType());
            assertThat(options.get(i).isRequired()).isEqualTo(OPTIONS.get(i).isRequired());
        }
    }

    @Test
    void testBuildReminderReceiptMessage() {
        AddReminderCommand addReminderCommand = new AddReminderCommand();
        MessageCreateData message =
                addReminderCommand.buildReminderReceiptMessage(
                        TITLE, REMINDER_TIME, OFFSET, DELAY, INTERVAL, UNIT);
        List<MessageEmbed> embeds = message.getEmbeds();
        assertThat(embeds.size()).isEqualTo(1);

        MessageEmbed embed = embeds.get(0);
        List<MessageEmbed.Field> fields = embed.getFields();
        assertThat(fields.size()).isEqualTo(6);
        assertThat(fields.get(0).getValue()).isEqualTo(TITLE);
        assertThat(fields.get(1).getValue()).isEqualTo(REMINDER_TIME);
        assertThat(fields.get(2).getValue()).isEqualTo(String.valueOf(OFFSET));
        assertThat(fields.get(3).getValue()).isEqualTo(String.valueOf(DELAY));
        assertThat(fields.get(4).getValue()).isEqualTo(String.valueOf(INTERVAL));
        assertThat(fields.get(5).getValue()).isEqualTo(UNIT);
    }
}
