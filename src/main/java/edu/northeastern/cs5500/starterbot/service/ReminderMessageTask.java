package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.exception.ReminderNotFoundException;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

/**
 * The task that runs repeatedly at the rate specified by the repeat interval and repeat time unit
 * of the associated reminder.
 */
@Slf4j
public class ReminderMessageTask implements Runnable {

    private String reminderId;
    private JDA jda;
    private ReminderEntryController reminderEntryController;

    public ReminderMessageTask(
            String reminderId, ReminderEntryController reminderEntryController, JDA jda) {
        this.reminderId = reminderId;
        this.reminderEntryController = reminderEntryController;
        this.jda = jda;
    }

    @Override
    public void run() {
        // load the reminder from database
        ReminderEntry retrivedEntry = reminderEntryController.getReminder(reminderId);

        // If the reminder is not there any more we don't do anything
        if (retrivedEntry == null) {
            return;
        }

        // Send the message
        String message =
                String.format(
                        "Hello <@%s>! You have %s coming up in %d minutes, get ready!",
                        retrivedEntry.getDiscordUserId(),
                        retrivedEntry.getTitle(),
                        retrivedEntry.getReminderOffset());

        String userId = retrivedEntry.getDiscordUserId();
        User user = jda.retrieveUserById(userId).complete();
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());

        // Delete reminder if it's one time
        if (retrivedEntry.getRepeatInterval() == null) {
            reminderEntryController.deleteReminder(reminderId);
        } else {
            // otherwise update the nextReminderTime
            String timeZone = retrivedEntry.getTimeZone();
            ZonedDateTime lastReminderTime =
                    retrivedEntry.getNextReminderTime().atZone(ZoneId.of(timeZone));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));
            LocalDateTime newNextReminderTime =
                    ReminderSchedulingService.getNextReminderTime(
                                    lastReminderTime,
                                    retrivedEntry.getRepeatTimeUnit(),
                                    retrivedEntry.getRepeatInterval(),
                                    now)
                            .toLocalDateTime();
            try {
                reminderEntryController.updateNextReminderTime(reminderId, newNextReminderTime);
            } catch (ReminderNotFoundException rnfe) {
                log.error(
                        "Could restart reminder with id {} because reminder no longer exists",
                        reminderId);
            }
        }
    }
}
