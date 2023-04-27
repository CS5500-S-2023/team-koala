package edu.northeastern.cs5500.starterbot.service.reminderService;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.ReminderNotFoundException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.service.Service;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

/**
 * ReminderSchedulingService handles actual scheduling of the message, restarting message for
 * existing reminders upon restart, and time calculations for reminders.
 */
@Singleton
@Slf4j
public class ReminderSchedulingService implements Service {
    public static final String TIME_ZONE = "America/Los_Angeles";
    private static final Integer THREAD_COUNT = 50;
    private static ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(THREAD_COUNT);
    private ReminderEntryController reminderEntryController;
    private JDA jda;

    @Inject
    public ReminderSchedulingService(ReminderEntryController reminderEntryController, JDA jda) {
        this.reminderEntryController = reminderEntryController;
        this.jda = jda;
        Runnable initializeTask = this::initializeReminders;
        if (jda != null) {
            scheduleTask(initializeTask, 0, null, null);
        }
    }

    /**
     * Restart the messages for exsiting reminders in the nearest future compared to now.
     *
     * @param now - time of execution / initialization.
     */
    public void initializeReminders() {
        ReminderEntry[] allReminders =
                reminderEntryController.getAllReminders().toArray(new ReminderEntry[0]);
        // for each reminder in the database, restart their messages
        for (ReminderEntry reminder : allReminders) {
            String timeZone = reminder.getTimeZone();
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));
            long initialDelay = initNextReminderTime(reminder, now);

            ReminderMessageTask messageTask =
                    new ReminderMessageTask(
                            reminder.getId().toString(), reminderEntryController, jda);
            if (jda != null) {
                try {
                    scheduleTask(
                            messageTask,
                            initialDelay,
                            reminder.getRepeatTimeUnit(),
                            reminder.getRepeatInterval());
                } catch (RejectedExecutionException ree) {
                    log.error(
                            "Could restart reminder with id {}, task rejected by scheduler",
                            reminder.getId());
                }
            }
        }
    }

    /**
     * Initiates / updates next reminder time for existing reminders upon bot restart.
     *
     * @param reminder - the reminder to update.
     * @param now - time of execution.
     * @return long - return the initial delay until the first reminder message.
     */
    public long initNextReminderTime(ReminderEntry reminder, ZonedDateTime now) {
        ZonedDateTime lastReminderTime =
                reminder.getNextReminderTime().atZone(ZoneId.of(reminder.getTimeZone()));
        TimeUnit repeatTimeUnit = reminder.getRepeatTimeUnit();
        Integer repeatInterval = reminder.getRepeatInterval();
        ZonedDateTime nextReminderTime =
                getNextReminderTime(lastReminderTime, repeatTimeUnit, repeatInterval, now);
        String reminderId = reminder.getId().toString();

        Duration durationTilNextReminder = Duration.between(now, nextReminderTime);

        long initialDelay = durationTilNextReminder.getSeconds();
        try {
            reminderEntryController.updateNextReminderTime(
                    reminderId, nextReminderTime.toLocalDateTime());
        } catch (ReminderNotFoundException rnfe) {
            log.error(
                    "Could restart reminder with id {} because reminder no longer exists",
                    reminderId);
        }
        return initialDelay;
    }

    /**
     * Schedules the message task to run at the rate defined by initialDelay, unit, and interval.
     *
     * @param messageTask - the task to be run (repeatedly).
     * @param initialDelay - the initial delay before the task runs for the first time.
     * @param unit - the time unit of the repeat interval of the task.
     * @param repeatInterval - interval between two reminder messages if the reminder repeats, its
     *     unit being the unit above.
     * @throws RejectedExecutionException - thrown by ScheduledExecutorService when the task cannot
     *     be scheduled.
     */
    public static void scheduleTask(
            Runnable messageTask, long initialDelay, TimeUnit unit, Integer repeatInterval)
            throws RejectedExecutionException {
        // Schdule the message(s)
        if (repeatInterval == null) {
            scheduler.schedule(messageTask, initialDelay, TimeUnit.SECONDS);
            return;
        }
        scheduler.scheduleAtFixedRate(
                messageTask, initialDelay, unit.toSeconds(1) * repeatInterval, TimeUnit.SECONDS);
    }

    /**
     * Gets the time the next reminder should be sent in the nearest future from now.
     *
     * @param reminderTimeActual - the actual message time = reminderTime - offset
     * @param unit - the time unit of the repeat interval of the reminder.
     * @param repeatInterval - interval between two reminder messages if the reminder repeats, its
     *     unit being the unit above.
     * @param now - time of execution / request.
     * @return ZonedDateTime - the time of the next reminder message.
     */
    public static ZonedDateTime getNextReminderTime(
            ZonedDateTime reminderTimeActual,
            TimeUnit unit,
            Integer repeatInterval,
            ZonedDateTime now) {
        ZonedDateTime nextReminder = reminderTimeActual;
        // For non-repeated reminders that are scheduled at a time that has
        // already passsed, advance by a day
        // E.g. scheduled a 10am reminder at 4pm, message arrives at 10am the following day.
        if (repeatInterval == null) {
            repeatInterval = 1;
            unit = TimeUnit.DAYS;
        }

        while (now.compareTo(nextReminder) >= 0) {
            nextReminder = plusInterval(nextReminder, unit, repeatInterval);
        }
        return nextReminder;
    }

    /**
     * Adds interval to given time based on specified time unit and interval.
     *
     * @param time - the time to add interval to.
     * @param unit - the time unit of the interval.
     * @param interval - the amount of time unit to be added.
     * @return ZonedDateTime - the updated time.
     */
    public static ZonedDateTime plusInterval(ZonedDateTime time, TimeUnit unit, Integer interval) {
        switch (unit) {
            case MINUTES:
                return time.plusMinutes(interval);
            case HOURS:
                return time.plusHours(interval);
            case DAYS:
                return time.plusDays(interval);
            default:
                return time.plusDays(interval);
        }
    }

    @Override
    public void register() {
        log.info("ReminderSchedulingService > register");
    }
}
