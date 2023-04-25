package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.NotYourPackageException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/** The command that allows the user to delete a package that belongs to the user */
@Singleton
@Slf4j
public class DeletePackageCommand implements SlashCommandHandler {

    @Inject PackageController packageController;

    @Inject
    public DeletePackageCommand() {
        // Defined empty and public for dagger injection
    }

    /**
     * Returns the name of the command
     *
     * @return String - the name of the command
     */
    @Override
    @Nonnull
    public String getName() {
        return "delete_package";
    }

    /**
     * Returns the name and options of this command
     *
     * @return CommandData - the command information
     */
    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Delete a package")
                .addOption(
                        OptionType.STRING,
                        "package_id",
                        "The bot will delete the package associated with the package ID",
                        true);
    }

    /**
     * When the user interacts with this command, an event occurs. The command checks the event
     * input while parsing the data for the packageId to determine which package to delete if those
     * inputs are valid, else exceptions will be caught and a message will be printed.
     *
     * @param event - user's interaction by inputting a packageId
     * @exception IllegalArgumentException caught if the packageId does not exist in the database
     * @exception NotYourPackageException caught if the packageId is valid but does not belong to
     *     the user
     */
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /delete_package");
        OptionMapping packageIdOption =
                Objects.requireNonNull(
                        event.getOption("package_id"),
                        "Received null value for mandatory parameter 'package_id'");

        String userId = event.getUser().getId();
        String packageId = packageIdOption.getAsString();

        try {
            packageController.deletePackage(packageId, userId);
        } catch (IllegalArgumentException e) {
            event.reply("This is not a valid package id!");
        } catch (NotYourPackageException e) {
            event.reply(e.getMessage()).queue();
        }

        event.reply("Your package has been deleted successfully").queue();
    }
}
