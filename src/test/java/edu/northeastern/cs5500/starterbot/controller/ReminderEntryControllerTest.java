package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ReminderEntryControllerTest {
    static final String DISCORD_USER_ID = "1071543384809930763";
    static final String TITLE = "Reminder";
    static final Integer REMINDER_OFFSET = 5;
    static final Integer REPEAT_INTERVAL = 60;

    static final LocalTime REMINDER_TIME = LocalTime.of(14, 00);
    static final TimeUnit REPEAT_TIME_UNIT = TimeUnit.MINUTES;

    private ReminderEntryController getReminderEntryController() {
        ReminderEntryController reminderEntryController =
                new ReminderEntryController(new InMemoryRepository<>());
        return reminderEntryController;
    }

    @Test
    void testAddReminder() {
        // setup
        ReminderEntryController reminderEntryController = getReminderEntryController();
        ReminderEntry testEntry =
                ReminderEntry.builder()
                        .discordUserId(DISCORD_USER_ID)
                        .title(TITLE)
                        .reminderTime(REMINDER_TIME)
                        .reminderOffset(REMINDER_OFFSET)
                        .repeatInterval(REPEAT_INTERVAL)
                        .repeatTimeUnit(REPEAT_TIME_UNIT)
                        .build();

        // mutation
        reminderEntryController.addReminder(testEntry);
        ReminderEntry savedEntry =
                reminderEntryController.getReminderEntryForUserId(DISCORD_USER_ID);

        // postcondition
        assertThat(savedEntry.getDiscordUserId()).isEqualTo(DISCORD_USER_ID);
        assertThat(savedEntry.getTitle()).isEqualTo(TITLE);
        assertThat(savedEntry.getReminderTime()).isEqualTo(REMINDER_TIME);
        assertThat(savedEntry.getReminderOffset()).isEqualTo(REMINDER_OFFSET);
        assertThat(savedEntry.getRepeatInterval()).isEqualTo(REPEAT_INTERVAL);
        assertThat(savedEntry.getRepeatTimeUnit()).isEqualTo(REPEAT_TIME_UNIT);
    }
}
