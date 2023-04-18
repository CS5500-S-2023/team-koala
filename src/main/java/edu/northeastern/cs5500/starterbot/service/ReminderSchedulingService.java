package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ReminderSchedulingService implements Service {
    public static final String TIME_ZONE = "America/Los_Angeles";
    private static final Integer THREAD_COUNT = 50;
    private static ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(THREAD_COUNT);
    ReminderEntryController reminderEntryController;

    @Inject
    public ReminderSchedulingService(ReminderEntryController reminderEntryController) {
        this.reminderEntryController = reminderEntryController;
        Collection<ReminderEntry> allReminders = reminderEntryController.getAllReminders();

        for (ReminderEntry reminder : allReminders) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(TIME_ZONE));
            LocalDateTime firstReminderTimeLocal = reminder.getFirstReminderTime();
            ZonedDateTime firstReminderTime = firstReminderTimeLocal.atZone(ZoneId.of(TIME_ZONE));
            TimeUnit repeatTimeUnit = reminder.getRepeatTimeUnit();
            Integer repeatInterval = reminder.getRepeatInterval();
            ZonedDateTime nextReminderTime =
                    getNextReminderTime(firstReminderTime, repeatTimeUnit, repeatInterval, now);
            Duration durationTilNextReminder = Duration.between(now, nextReminderTime);
            System.out.println(nextReminderTime + " " + reminder.getId().toString());

            long initialDelay = durationTilNextReminder.getSeconds();
            ReminderMessageTask messageTask = new ReminderMessageTask(reminder.getId().toString());
            scheduleReminder(messageTask, initialDelay, repeatTimeUnit, repeatInterval);
        }
    }

    public void scheduleReminder(
            ReminderMessageTask messageTask,
            long initialDelay,
            TimeUnit unit,
            Integer repeatInterval) {
            System.out.println(initialDelay);
            // Runnable task =
            //     new Runnable() {
            //         @Override
            //         public void run() {
            //             System.out.println("Hello");
            //         }
            //     };
        // Schdule the message(s)
        if (repeatInterval == null) {
            scheduler.schedule(messageTask, initialDelay, TimeUnit.SECONDS);
            return;
        }
        scheduler.scheduleAtFixedRate(
                messageTask, initialDelay, unit.toSeconds(1) * repeatInterval, TimeUnit.SECONDS);
    }

    /**
     * Reminder messages start the next time the clock hits reminderTime e.g. now is 10am the user
     * scheduled a reminder at 8am then the first reminder message will be at 8am the next day
     *
     * @param reminderTimeActual - the actual message time = reminderTime - offset
     * @param unit - TimeUnit of repeating reminders
     * @param repeatInterval - interval between two reminder messages if the reminder repeats
     * @param now - time of execution
     * @return ZonedDateTime - the time of the next reminder message
     */
    public ZonedDateTime getNextReminderTime(
            ZonedDateTime reminderTimeActual,
            TimeUnit unit,
            Integer repeatInterval,
            ZonedDateTime now) {
        ZonedDateTime nextReminder =
                now.withHour(reminderTimeActual.getHour())
                        .withMinute(reminderTimeActual.getMinute());
        if (repeatInterval == null) {
            repeatInterval = 1;
            unit = TimeUnit.DAYS;
        }

        int today = now.getDayOfMonth();
        while (now.compareTo(nextReminder) >= 0 && nextReminder.getDayOfMonth() == today) {
            switch (unit) {
                case MINUTES:
                    nextReminder = nextReminder.plusMinutes(repeatInterval);
                    break;
                case HOURS:
                    nextReminder = nextReminder.plusHours(repeatInterval);
                    break;
                case DAYS:
                    nextReminder = nextReminder.plusDays(repeatInterval);
                    break;
                default:
                    break;
            }
        }
        return nextReminder;
    }

    @Override
    public void register() {
        log.info("ReminderSchedulingService > register");
    }
}
