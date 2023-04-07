package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
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
    static final TimeUnit REPEAT_TIME_UNIT = TimeUnit.MINUTES;
    private ReminderEntryController reminderEntryController;

    private ReminderEntryController getReminderEntryController() {
        return new ReminderEntryController(new InMemoryRepository<>());
    }

    @BeforeEach
    void beforeEach() {
        // setup
        reminderEntryController = getReminderEntryController();

        // mutation
        reminderEntryController.addReminder(
                DISCORD_USER_ID,
                TITLE,
                REMINDER_TIME,
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
