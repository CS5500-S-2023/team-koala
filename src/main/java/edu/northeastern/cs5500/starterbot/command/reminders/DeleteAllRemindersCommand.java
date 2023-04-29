package edu.northeastern.cs5500.starterbot.command.reminders;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.ReminderEntryController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/** The command deletes all reminders in the database. For developement testing. */
@Singleton
@Slf4j
public class DeleteAllRemindersCommand implements SlashCommandHandler {
    @Inject ReminderEntryController reminderEntryController;

    @Inject
    public DeleteAllRemindersCommand() {
        // empty as everything is injected
    }

    /**
     * Returns the name of the command.
     *
     * @return String - the name of the command.
     */
    @Override
    @Nonnull
    public String getName() {
        return "delete_all_reminders";
    }

    /**
     * Returns the name and options of this command.
     *
     * @return CommandData - information about this command
     */
    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Deletes all reminders");
    }

    /**
     * When user interacts with this command ('event' happens) The command deletes all reminders in
     * the database.
     *
     * <p>This is a temporary solution for testing. To be removed in the future.
     *
     * @param event - user's interaction event
     */
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /delete_all_reminders");

        reminderEntryController.deleteAllReminders();

        event.reply("All reminders has been successfully deleted!");
    }
}
