package edu.northeastern.cs5500.starterbot.service.reminderService;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.ReminderException.UnableToAddReminderException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReminderSchedulingServiceTest {
    static final String DISCORD_USER_ID = "1071543384809930763";
    static final String TITLE = "Reminder";
    static final Integer REMINDER_OFFSET = 5;
    static final Integer REPEAT_INTERVAL = 60;

    static final LocalTime REMINDER_TIME = LocalTime.of(14, 00);
    static final LocalDateTime NEXT_REMINDER_TIME = LocalDateTime.of(2023, 04, 17, 1, 0, 0);
    static final TimeUnit REPEAT_TIME_UNIT = TimeUnit.MINUTES;
    static final String TIME_ZONE = "America/Los_Angeles";
    private ZonedDateTime now;
    ReminderSchedulingService reminderSchedulingService;

    @BeforeEach
    void beforeEach() {
        now = ZonedDateTime.now(ZoneId.of(TIME_ZONE));
        now = now.withYear(2023).withMonth(4).withDayOfMonth(18).withHour(1).withMinute(0);
    }

    @Test
    void testInitNextReminderTime() throws UnableToAddReminderException {
        ReminderEntryController testController =
                new ReminderEntryController(new InMemoryRepository<>());
        ReminderEntry testEntry =
                ReminderEntry.builder()
                        .discordUserId(DISCORD_USER_ID)
                        .title(TITLE)
                        .reminderTime(REMINDER_TIME)
                        .reminderOffset(REMINDER_OFFSET)
                        .nextReminderTime(NEXT_REMINDER_TIME)
                        .repeatInterval(REPEAT_INTERVAL)
                        .repeatTimeUnit(REPEAT_TIME_UNIT)
                        .timeZone(TIME_ZONE)
                        .build();
        testEntry = testController.addReminder(testEntry);
        String reminderId = testEntry.getId().toString();
        reminderSchedulingService = new ReminderSchedulingService(testController, null);
        reminderSchedulingService.initNextReminderTime(testEntry, now);
        ReminderEntry updatedEntry = testController.getReminder(reminderId);
        LocalDateTime updatedTime = updatedEntry.getNextReminderTime();
        assertThat(updatedTime.getDayOfMonth()).isEqualTo(18);
        assertThat(updatedTime.getHour()).isEqualTo(2);
        assertThat(updatedTime.getMinute()).isEqualTo(0);
    }

    @Test
    void testGetNextReminderTime() {

        ZonedDateTime lastReminderTime = now.withDayOfMonth(17).withHour(0).withMinute(57);

        ZonedDateTime timeMinute =
                ReminderSchedulingService.getNextReminderTime(
                        lastReminderTime, TimeUnit.MINUTES, 10, now);
        assertThat(timeMinute.getDayOfMonth()).isEqualTo(18);
        assertThat(timeMinute.getHour()).isEqualTo(1);
        assertThat(timeMinute.getMinute()).isEqualTo(7);

        ZonedDateTime timeHour =
                ReminderSchedulingService.getNextReminderTime(
                        lastReminderTime, TimeUnit.HOURS, 1, now);
        assertThat(timeHour.getDayOfMonth()).isEqualTo(18);
        assertThat(timeHour.getHour()).isEqualTo(1);
        assertThat(timeHour.getMinute()).isEqualTo(57);

        ZonedDateTime timeDay =
                ReminderSchedulingService.getNextReminderTime(
                        lastReminderTime, TimeUnit.DAYS, 1, now);
        assertThat(timeDay.getDayOfMonth()).isEqualTo(19);
        assertThat(timeDay.getHour()).isEqualTo(0);
        assertThat(timeDay.getMinute()).isEqualTo(57);
    }

    @Test
    void testPlusInterval() {

        ZonedDateTime plusMinutes =
                ReminderSchedulingService.plusInterval(now, TimeUnit.MINUTES, 10);
        assertThat(plusMinutes.getDayOfMonth()).isEqualTo(18);
        assertThat(plusMinutes.getHour()).isEqualTo(1);
        assertThat(plusMinutes.getMinute()).isEqualTo(10);

        ZonedDateTime plusHours = ReminderSchedulingService.plusInterval(now, TimeUnit.HOURS, 10);
        assertThat(plusHours.getDayOfMonth()).isEqualTo(18);
        assertThat(plusHours.getHour()).isEqualTo(11);
        assertThat(plusHours.getMinute()).isEqualTo(0);

        ZonedDateTime plusDays = ReminderSchedulingService.plusInterval(now, TimeUnit.DAYS, 10);
        assertThat(plusDays.getDayOfMonth()).isEqualTo(28);
        assertThat(plusDays.getHour()).isEqualTo(1);
        assertThat(plusDays.getMinute()).isEqualTo(0);
    }
}
