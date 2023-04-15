package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.bson.types.ObjectId;

/** The controller that interacts with the database and handles some of the input validation */
public class ReminderEntryController {
    GenericRepository<ReminderEntry> reminderEntryRepository;

    @Inject
    ReminderEntryController(GenericRepository<ReminderEntry> reminderEntryRepository) {
        this.reminderEntryRepository = reminderEntryRepository;
    }

    /**
     * Returns the repeat time unit in type TimeUnit after parsing String input from user.
     *
     * @param unitString - the string input specifying the repeat time unit
     * @return TimeUnit - the time unit in proper type
     * @throws InvalidTimeUnitException - thrown when the input is not m (minute) / h (hour) / d
     *     (day)
     */
    public static TimeUnit parseTimeUnit(String unitString) throws InvalidTimeUnitException {
        TimeUnit unit = null;
        switch (unitString) {
            case "m":
                unit = TimeUnit.MINUTES;
                break;

            case "h":
                unit = TimeUnit.HOURS;
                break;

            case "d":
                unit = TimeUnit.DAYS;
                break;

            default:
                throw new InvalidTimeUnitException(
                        "Repeat time unit can only be m(minute) / h(hour) / d(day)");
        }
        return unit;
    }

    /**
     * Adds a new ReminderEntry with specified info to the database.
     *
     * @param discordUserId - the user who added the reminder
     * @param title - title of the reminder
     * @param reminderTime - time of the reminder
     * @param offset - how much earlier do the user wants to be reminded
     * @param interval - the interval between 2 reminder messages if reminder repeats
     * @param unit - the time unit of the repeat interval
     * @return ReminderEntry - the saved ReminderEntry.
     */
    public ReminderEntry addReminder(
            String discordUserId,
            String title,
            LocalTime reminderTime,
            Integer offset,
            Integer interval,
            TimeUnit unit) {
        ReminderEntry reminderEntry =
                ReminderEntry.builder()
                        .discordUserId(discordUserId)
                        .title(title)
                        .reminderTime(reminderTime)
                        .reminderOffset(offset)
                        .repeatInterval(interval)
                        .repeatTimeUnit(unit)
                        .build();
        return reminderEntryRepository.add(reminderEntry);
    }

    /**
     * Returns all reminders for user specified by discordUserId.
     *
     * @param discordUserId - the specified user
     * @return Collection<ReminderEntry> - all reminders for the specified user
     */
    public Collection<ReminderEntry> getRemindersForUser(String discordUserId) {
        Collection<ReminderEntry> reminderEntries = reminderEntryRepository.getAll();
        Collection<ReminderEntry> remindersForUser = new ArrayList<>();
        for (ReminderEntry reminderEntry : reminderEntries) {
            if (reminderEntry.getDiscordUserId().equals(discordUserId)) {
                remindersForUser.add(reminderEntry);
            }
        }

        return remindersForUser;
    }

    /**
     * Returns reminder with id reminderId.
     *
     * @param reminderId - the id of the reminder to be found.
     * @return ReminderEntry - the reminder with reminderId or null if it doesn't exist
     */
    public ReminderEntry getReminder(String reminderId) {
        return reminderEntryRepository.get(new ObjectId(reminderId));
    }

    /**
     * Deletes reminder with reminderId from database.
     *
     * @param reminderId - the id of the reminder to be deleted.
     */
    public void deleteReminder(String reminderId) {
        ObjectId id = new ObjectId(reminderId);
        reminderEntryRepository.delete(id);
    }
}
