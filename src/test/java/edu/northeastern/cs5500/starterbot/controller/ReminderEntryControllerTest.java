package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReminderEntryControllerTest {
    static final String DISCORD_USER_ID = "1071543384809930763";
    static final String TITLE = "Reminder";
    static final Integer REMINDER_OFFSET = 5;
    static final Integer REPEAT_INTERVAL = 60;

    static final LocalTime REMINDER_TIME = LocalTime.of(14, 00);
    static final LocalDateTime FIRST_REMINDER_TIME = LocalDateTime.of(2023, 04, 17, 1, 0, 0);
    static final TimeUnit REPEAT_TIME_UNIT = TimeUnit.MINUTES;
    private ReminderEntryController reminderEntryController;
    private ReminderEntry testEntry;

    private ReminderEntryController getReminderEntryController() {
        return new ReminderEntryController(new InMemoryRepository<>());
    }

    @BeforeEach
    void beforeEach() {
        // setup
        reminderEntryController = getReminderEntryController();

        // mutation
        testEntry =
                reminderEntryController.addReminder(
                        DISCORD_USER_ID,
                        TITLE,
                        REMINDER_TIME,
                        FIRST_REMINDER_TIME,
                        REMINDER_OFFSET,
                        REPEAT_INTERVAL,
                        REPEAT_TIME_UNIT);
    }

    @Test
    void testParseTimeUnit() throws InvalidTimeUnitException {
        assertThat(ReminderEntryController.parseTimeUnit("m")).isEqualTo(TimeUnit.MINUTES);
        assertThat(ReminderEntryController.parseTimeUnit("h")).isEqualTo(TimeUnit.HOURS);
        assertThat(ReminderEntryController.parseTimeUnit("d")).isEqualTo(TimeUnit.DAYS);
        assertThrows(
                InvalidTimeUnitException.class, () -> ReminderEntryController.parseTimeUnit("r"));
    }

    @Test
    void testAddReminder() {

        ReminderEntry[] reminders =
                reminderEntryController
                        .getRemindersForUser(DISCORD_USER_ID)
                        .toArray(new ReminderEntry[0]);
        ReminderEntry savedEntry = reminders[0];

        assertThat(savedEntry).isNotNull();
        assertThat(savedEntry).isEqualTo(testEntry);
    }

    @Test
    void testGetRemindersForUser() {
        ReminderEntry[] reminders =
                reminderEntryController
                        .getRemindersForUser(DISCORD_USER_ID)
                        .toArray(new ReminderEntry[0]);

        assertThat(reminders.length).isGreaterThan(0);
        for (ReminderEntry reminder : reminders) {
            assertThat(reminder.getDiscordUserId()).isEqualTo(DISCORD_USER_ID);
        }
    }

    @Test
    void testGetReminder() {
        String reminderId = testEntry.getId().toString();

        ReminderEntry retrievedEntry = reminderEntryController.getReminder(reminderId);

        assertThat(retrievedEntry).isNotNull();
        assertThat(retrievedEntry).isEqualTo(testEntry);
    }

    @Test
    void testDeleteReminder() {
        String reminderId = testEntry.getId().toString();
        reminderEntryController.deleteReminder(reminderId);
        ReminderEntry retrievedEntry = reminderEntryController.getReminder(reminderId);
        assertThat(retrievedEntry).isNull();
    }
}
