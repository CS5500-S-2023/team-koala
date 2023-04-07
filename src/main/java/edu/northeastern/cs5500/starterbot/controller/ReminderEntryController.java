package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.bson.types.ObjectId;

public class ReminderEntryController {
    GenericRepository<ReminderEntry> reminderEntryRepository;

    @Inject
    ReminderEntryController(GenericRepository<ReminderEntry> reminderEntryRepository) {
        this.reminderEntryRepository = reminderEntryRepository;
    }

    public static LocalTime parseReminderTime(String timeString) throws DateTimeParseException {
        return LocalTime.parse(timeString);
    }

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

    public void addReminder(
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
        reminderEntryRepository.add(reminderEntry);
    }

    public ReminderEntry getReminderEntryForUserId(String discordUserId) {
        Collection<ReminderEntry> reminderEntries = reminderEntryRepository.getAll();
        for (ReminderEntry reminderEntry : reminderEntries) {
            if (reminderEntry.getDiscordUserId().equals(discordUserId)) {
                return reminderEntry;
            }
        }

        return null;
    }

    public void deleteReminder(String reminderId) {
        ObjectId id = new ObjectId(reminderId);
        reminderEntryRepository.delete(id);
    }
}
