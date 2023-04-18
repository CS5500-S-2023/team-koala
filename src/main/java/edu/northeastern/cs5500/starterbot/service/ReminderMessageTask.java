package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.Bot;
import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;

public class ReminderMessageTask implements Runnable {

    private String reminderId;
    @Inject ReminderEntryController reminderEntryController;

    @Inject
    public ReminderMessageTask(String reminderId) {
        this.reminderId = reminderId;
    }

    @Override
    public void run() {
        // System.out.println("loading reminder: " + reminderId);
        // // load the reminder from database
        // ReminderEntry retrivedEntry = reminderEntryController.getReminder(reminderId);

        // // If the reminder is not there any more we don't do anything
        // if (retrivedEntry == null) {
        //     System.out.println("null: returning...");
        //     return;
        // }

        // System.out.println("Before message");
        // // Send the message
        // String message =
        //         String.format(
        //                 "Hello <@%s>! You have %s coming up in %d minutes, get ready!",
        //                 retrivedEntry.getDiscordUserId(),
        //                 retrivedEntry.getTitle(),
        //                 retrivedEntry.getReminderOffset());

        // System.out.println(message);
        // JDA jda = Bot.getJDA();
        // //String userId = retrivedEntry.getDiscordUserId();
        // // for (Guild guild : jda.getGuilds()) {
        // //     guild.getDefaultChannel().asTextChannel().sendMessage(message).queue();
        // // }
        // User user = jda.getUserById(userId);
        // user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());

        // // Delete reminder if it's one time
        // if (retrivedEntry.getRepeatInterval() == null) {
        //     reminderEntryController.deleteReminder(reminderId);
        // }
    }
}
