package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.exception.ReminderNotFoundException;
import edu.northeastern.cs5500.starterbot.exception.UnableToAddReminderException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReminderEntryControllerTest {
    static final String DISCORD_USER_ID = "1071543384809930763";
    static final String TITLE = "Reminder";
    static final Integer REMINDER_OFFSET = 5;
    static final Integer REPEAT_INTERVAL = 60;

    static final LocalTime REMINDER_TIME = LocalTime.of(14, 00);
    static final LocalDateTime NEXT_REMINDER_TIME = LocalDateTime.of(2023, 04, 17, 1, 0, 0);
    static final TimeUnit REPEAT_TIME_UNIT = TimeUnit.MINUTES;
    static final String TIME_ZONE = "America/Los_Angeles";
    static final String MALFORMED_ID = "someid1234567";
    static final String INVALID_ID = new ObjectId().toString();
    private ReminderEntryController reminderEntryController;
    private ReminderEntry testEntry;

    private ReminderEntryController getReminderEntryController() {
        return new ReminderEntryController(new InMemoryRepository<>());
    }

    @BeforeEach
    void beforeEach() throws UnableToAddReminderException {
        // setup
        reminderEntryController = getReminderEntryController();

        ReminderEntry reminderEntry =
                ReminderEntry.builder()
                        .discordUserId(DISCORD_USER_ID)
                        .title(TITLE)
                        .reminderTime(REMINDER_TIME)
                        .nextReminderTime(NEXT_REMINDER_TIME)
                        .reminderOffset(REMINDER_OFFSET)
                        .timeZone(TIME_ZONE)
                        .repeatInterval(REPEAT_INTERVAL)
                        .repeatTimeUnit(REPEAT_TIME_UNIT)
                        .build();

        // mutation
        testEntry = reminderEntryController.addReminder(reminderEntry);
    }

    @Test
    void testParseTimeUnit() throws InvalidTimeUnitException {
        assertThat(ReminderEntryController.parseTimeUnit("m")).isEqualTo(TimeUnit.MINUTES);
        assertThat(ReminderEntryController.parseTimeUnit("h")).isEqualTo(TimeUnit.HOURS);
        assertThat(ReminderEntryController.parseTimeUnit("d")).isEqualTo(TimeUnit.DAYS);
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
        assertThrows(
                IllegalArgumentException.class,
                () -> reminderEntryController.getReminder(MALFORMED_ID));
    }

    @Test
    void testDeleteReminder() throws IllegalArgumentException {
        String reminderId = testEntry.getId().toString();
        reminderEntryController.deleteReminder(reminderId);
        ReminderEntry retrievedEntry = reminderEntryController.getReminder(reminderId);
        assertThat(retrievedEntry).isNull();
        assertThrows(
                IllegalArgumentException.class,
                () -> reminderEntryController.deleteReminder(MALFORMED_ID));
    }

    @Test
    void testUpdateNextReminderTime() throws ReminderNotFoundException {
        String reminderId = testEntry.getId().toString();
        LocalDateTime newNextReminderTime = NEXT_REMINDER_TIME.plusMinutes(20);
        reminderEntryController.updateNextReminderTime(reminderId, newNextReminderTime);
        ReminderEntry retrievedEntry = reminderEntryController.getReminder(reminderId);
        assertThat(retrievedEntry.getNextReminderTime()).isEqualTo(newNextReminderTime);
        assertThrows(
                ReminderNotFoundException.class,
                () ->
                        reminderEntryController.updateNextReminderTime(
                                INVALID_ID, newNextReminderTime));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        reminderEntryController.updateNextReminderTime(
                                MALFORMED_ID, newNextReminderTime));
    }
}
