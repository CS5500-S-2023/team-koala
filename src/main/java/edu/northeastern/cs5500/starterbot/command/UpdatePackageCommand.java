package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.NotYourPackageException;
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

@Singleton
@Slf4j
public class UpdatePackageCommand implements SlashCommandHandler {

    @Inject PackageController packageController;

    @Inject
    public UpdatePackageCommand() {
        // Defined empty and public for dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "update_package";
    }

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
                        "The bot will record the name for the package",
                        false)
                .addOption(
                        OptionType.STRING,
                        "carrier_id",
                        "The bot will record the name for the package",
                        false);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /update_package");
        OptionMapping packageIdOption =
                Objects.requireNonNull(
                        event.getOption("package_id"),
                        "Received null value for mandatory parameter 'package_id'");

        String userId = event.getUser().getId();
        String packageId = packageIdOption.getAsString();
        Package p = null;
        try {
            p = packageController.getPackage(packageId);
        } catch (IllegalArgumentException e) {
            event.reply("This is not a valid package id!").queue();
        }

        String name = event.getOption("package_name", OptionMapping::getAsString);
        String trackingNumber = event.getOption("tracking_number", OptionMapping::getAsString);
        String carrierId = event.getOption("carrier_id", OptionMapping::getAsString);

        if (name == null) name = p.getName();
        if (trackingNumber == null) trackingNumber = p.getTrackingNumber();
        if (carrierId == null) carrierId = p.getCarrierId();

        try {
            packageController.updatePackage(packageId, userId, name, trackingNumber, carrierId);
        } catch (NotYourPackageException e) {
            event.reply(e.getMessage()).queue();
        } catch (IllegalArgumentException e) {
            event.reply("This is not a valid package id!").queue();
        }

        event.reply("Your package has been updated successfully").queue();
    }
}
