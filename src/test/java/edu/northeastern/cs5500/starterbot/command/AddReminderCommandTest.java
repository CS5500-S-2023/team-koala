package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

class AddReminderCommandTest {

    static final List<OptionData> OPTIONS =
            Arrays.asList(
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

    @Test
    void testNameMatchesData() {
        AddReminderCommand addReminderCommand = new AddReminderCommand();
        String name = addReminderCommand.getName();
        SlashCommandData commandData = (SlashCommandData) addReminderCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
        List<OptionData> options = commandData.getOptions();
        assertThat(options.size()).isEqualTo(6);

        for (int i = 0; i < options.size(); i++) {
            assertThat(options.get(i).getName().equals(OPTIONS.get(i).getName()));
            assertThat(options.get(i).getType().equals(OPTIONS.get(i).getType()));
            assertThat(options.get(i).isRequired() == OPTIONS.get(i).isRequired());
        }
    }

    //     @Test
    //     void testGetNextReminderTime() {
    //         ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
    //         now = now.withHour(1).withMinute(0);
    //         AddReminderCommand addReminderCommand = new AddReminderCommand();

    //         ZonedDateTime timeMinute =
    //                 addReminderCommand.getNextReminderTime(
    //                         LocalTime.of(0, 57), TimeUnit.MINUTES, 10, now);
    //         ZonedDateTime expectedTimeMinute = now.withHour(1).withMinute(7);
    //         assertThat(timeMinute).isEqualTo(expectedTimeMinute);

    //         ZonedDateTime timeHour =
    //                 addReminderCommand.getNextReminderTime(LocalTime.of(0, 57), TimeUnit.HOURS,
    // 1, now);
    //         ZonedDateTime expectedTimeHour = now.withHour(1).withMinute(57);
    //         assertThat(timeHour).isEqualTo(expectedTimeHour);

    //         ZonedDateTime timeDay =
    //                 addReminderCommand.getNextReminderTime(LocalTime.of(0, 57), TimeUnit.DAYS, 1,
    // now);
    //         ZonedDateTime expectedTimeDay = now.withHour(0).withMinute(57).plusDays(1);
    //         assertThat(timeDay).isEqualTo(expectedTimeDay);

    //         ZonedDateTime timeNonRepeat =
    //                 addReminderCommand.getNextReminderTime(LocalTime.of(0, 57), null, null, now);
    //         ZonedDateTime expectedTimeNonRepeat = now.withHour(0).withMinute(57).plusDays(1);
    //         assertThat(timeNonRepeat).isEqualTo(expectedTimeNonRepeat);
    //     }
}
