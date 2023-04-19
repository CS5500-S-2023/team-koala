package edu.northeastern.cs5500.starterbot.service;

import static com.google.common.truth.Truth.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class ReminderSchedulingServiceTest {

    @Test
    void testGetNextReminderTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        now = now.withYear(2023).withMonth(4).withDayOfMonth(18).withHour(1).withMinute(0);

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
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        now = now.withYear(2023).withMonth(4).withDayOfMonth(18).withHour(1).withMinute(0);

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
