package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ReminderEntryControllerTest {
    static final String DISCORD_USER_ID = "1071543384809930763";
    static final String EVENT_TITLE = "Event";
    static final String REMINDER_TIME_STRING = "14:00";
    static final Integer REMINDER_OFFSET = 5;
    static final Integer RECURRENCE_INTERVAL = 60;
    static final String RECURRENCE_TIME_UNIT_STRING = "m";

    static final LocalTime REMINDER_TIME = LocalTime.of(14, 00);
    static final TimeUnit RECURRENCE_TIME_UNIT = TimeUnit.MINUTES;

    private ReminderEntryController getReminderEntryController() {
        ReminderEntryController reminderEntryController =
                new ReminderEntryController(new InMemoryRepository<>());
        // ReminderEntryController reminderEntryController =
        //         new ReminderEntryController(new InMemoryRepository<>());
        // System.out.println(reminderEntryController);
        return reminderEntryController;
    }

    @Test
    void testAddReminder() {
        // setup
        ReminderEntryController reminderEntryController = getReminderEntryController();

        // mutation
        reminderEntryController.addReminder(
                DISCORD_USER_ID,
                EVENT_TITLE,
                REMINDER_TIME_STRING,
                REMINDER_OFFSET,
                RECURRENCE_INTERVAL,
                RECURRENCE_TIME_UNIT_STRING);

        ReminderEntry savedEntry =
                reminderEntryController.getReminderEntryForUserId(DISCORD_USER_ID);

        // postcondition
        assertThat(savedEntry.getDiscordUserId()).isEqualTo(DISCORD_USER_ID);
        assertThat(savedEntry.getTitle()).isEqualTo(EVENT_TITLE);
        assertThat(savedEntry.getReminderTime()).isEqualTo(REMINDER_TIME);
        assertThat(savedEntry.getReminderOffset()).isEqualTo(REMINDER_OFFSET);
        assertThat(savedEntry.getRecurrenceInterval()).isEqualTo(RECURRENCE_INTERVAL);
        assertThat(savedEntry.getRecurrencTimeUnit()).isEqualTo(RECURRENCE_TIME_UNIT);
    }
}
