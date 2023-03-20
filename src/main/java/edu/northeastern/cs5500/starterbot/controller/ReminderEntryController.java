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
            ReminderEntry reminderEntry) {

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
