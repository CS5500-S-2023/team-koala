package edu.northeastern.cs5500.starterbot.command.PackageCommands;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.NotYourPackageException;
import edu.northeastern.cs5500.starterbot.exception.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
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

/** The command that allows the user to update a package that belongs to the user */
@Singleton
@Slf4j
public class UpdatePackageCommand implements SlashCommandHandler {

    @Inject PackageController packageController;

    @Inject
    public UpdatePackageCommand() {
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
        return "update_package";
    }

    /**
     * Returns the name and options of this command
     *
     * @return CommandData - the command information
     */
    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Update a package")
                .addOption(
                        OptionType.STRING,
                        "package_id",
                        "The bot will update the package associated with the package ID",
                        true)
                .addOption(
                        OptionType.STRING,
                        "package_name",
                        "The bot will record the name for the package",
                        false)
                .addOption(
                        OptionType.STRING,
                        "tracking_number",
                        "The bot will record the tracking number for the package",
                        false)
                .addOption(
                        OptionType.STRING,
                        "carrier_id",
                        "The bot will record the carrier id for the package",
                        false);
    }

    /**
     * When the user interacts with this command, an event occurs. The command checks the event
     * input while parsing the data for the packageId, package name, tracking number, and carrier id
     * to determine which package to update and what properties to update if valid, else exceptions
     * will be caught and a message will be printed.
     *
     * @param event - user's interaction by inputting a packageId, and optional name, tracking
     *     number, and carrier id
     * @exception IllegalArgumentException caught if the packageId does not exist in the database
     * @exception NotYourPackageException caught if the packageId is valid but does not belong to
     *     the user
     * @exception PackageNotExistException caught if the package carrier and tracking number
     *     combination are not a correct
     */
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /update_package");
        OptionMapping packageIdOption =
                Objects.requireNonNull(
                        event.getOption("package_id"),
                        "Received null value for mandatory parameter 'package_id'");

        String userId = event.getUser().getId();
        String packageId = packageIdOption.getAsString();
        Package p = packageController.getPackage(packageId);

        String name = event.getOption("package_name", OptionMapping::getAsString);
        String trackingNumber = event.getOption("tracking_number", OptionMapping::getAsString);
        String carrierId = event.getOption("carrier_id", OptionMapping::getAsString);

        if (name == null) name = p.getName();
        if (trackingNumber == null) trackingNumber = p.getTrackingNumber();
        if (carrierId == null) carrierId = p.getCarrierId();

        try {
            packageController.updatePackage(packageId, userId, name, trackingNumber, carrierId);
        } catch (NotYourPackageException e) {
            event.reply("This is not your package to update!").queue();
        } catch (IllegalArgumentException e) {
            event.reply("This is not a valid package id!").queue();
        } catch (PackageNotExistException e) {
            event.reply("This package has an invalid carrier and/or tracking number!").queue();
        }

        event.reply("Your package has been updated successfully").queue();
    }
}
