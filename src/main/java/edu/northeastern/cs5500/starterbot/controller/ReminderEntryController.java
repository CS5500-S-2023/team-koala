package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.exception.InvalidTimeUnitException;
import edu.northeastern.cs5500.starterbot.exception.ReminderNotFoundException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class ReminderEntryController {
    Map<String, ReminderEntry> tempStore = new HashMap<>();
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
        tempStore.put(discordUserId, reminderEntry);
    }

    public void confirmReminder(String discordUserId) throws ReminderNotFoundException {
        ReminderEntry entry = getPendingReminderForUserId(discordUserId);
        if (entry == null) {
            throw new ReminderNotFoundException("Cannot find pending reminder for user");
        }
        reminderEntryRepository.add(entry);
    }

    public void cancelReminder(String discordUserId) {
        if (tempStore.containsKey(discordUserId)) {
            tempStore.remove(discordUserId);
        }
    }

    public ReminderEntry getPendingReminderForUserId(String discordUserId) {
        return tempStore.get(discordUserId);
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
}
