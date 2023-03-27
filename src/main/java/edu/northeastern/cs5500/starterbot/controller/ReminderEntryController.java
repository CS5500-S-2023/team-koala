package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class ReminderEntryController {

    GenericRepository<ReminderEntry> reminderEntryRepository;

    @Inject
    ReminderEntryController(GenericRepository<ReminderEntry> reminderEntryRepository) {
        this.reminderEntryRepository = reminderEntryRepository;
    }

    public void addReminder(
            String discordUserId,
            String title,
            String reminderTimeString,
            Integer offset,
            Integer interval,
            String unitString) {

        // TODO: boweill - add more format validation here
        String[] reminderHourMin = reminderTimeString.split(":");
        Integer hour = Integer.parseInt(reminderHourMin[0]);
        Integer min = Integer.parseInt(reminderHourMin[1]);

        LocalTime reminderTime = LocalTime.of(hour, min);

        TimeUnit unit = null;
        if (interval != null) {
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
                    unit = TimeUnit.MINUTES;
                    break;
            }
        }
        ReminderEntry reminderEntry =
                ReminderEntry.builder()
                        .discordUserId(discordUserId)
                        .title(title)
                        .reminderTime(reminderTime)
                        .reminderOffset(offset)
                        .recurrenceInterval(interval)
                        .recurrencTimeUnit(unit)
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
}
