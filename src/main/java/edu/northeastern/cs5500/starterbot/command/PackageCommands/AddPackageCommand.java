package edu.northeastern.cs5500.starterbot.command.PackageCommands;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.command.StringSelectHandler;
import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.MissingMandatoryFieldsException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.*;

@Singleton
@Slf4j
public class AddPackageCommand implements SlashCommandHandler, StringSelectHandler {

    @Inject PackageController packageController;

    @Inject
    public AddPackageCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "add_package";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Add a new package")
                .addOption(
                        OptionType.STRING,
                        "tracking_number",
                        "The bot will record the number",
                        true)
                .addOption(
                        OptionType.STRING,
                        "package_name",
                        "The bot will record the name for the package",
                        false);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /add_package");

        // retrieve option data
        OptionMapping nameOption = event.getOption("package_name");
        OptionMapping trackingNumberOption =
                Objects.requireNonNull(
                        event.getOption("tracking_number"),
                        "Received null value for mandatory parameter 'tracking_number'");

        // collect package data
        String packageName = "";
        if (nameOption != null) {
            packageName = nameOption.getAsString();
        }
        String trackingNumber = trackingNumberOption.getAsString();
        log.info(
                "Collected data: packageName - {}, trackingNumber - {}",
                packageName,
                trackingNumber);

        // Reply with a select menu for users to choose a carrier
        StringSelectMenu.Builder carrierBuilder = StringSelectMenu.create("add_package");
        for (Map.Entry<String, String> entry : TrackPackageService.carrieMap.entrySet()) {
            carrierBuilder.addOption(
                    entry.getKey(),
                    String.format(
                            "%s::%s::%s",
                            packageName, trackingNumber, entry.getValue())); // label, value
        }

        event.reply("Select the carrier for your package")
                .addActionRow(carrierBuilder.build())
                .queue();
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        log.info("event: /add_package:StringSelectInteractionEvent - {}", event.getValues().get(0));

        // collect passed in data from previous step
        String params = event.getValues().get(0);
        Package builtPacakge = new Package();
        try {
            builtPacakge = buildPackage(params, event.getUser().getId());
        } catch (MissingMandatoryFieldsException e) {
            event.reply(String.format(
                                    "%s %s",
                                    e.getMessage(),
                                    PackageController.TRY_AGAIN_MESSAGE))
                    .queue();
            return;
        }
        

        // create a package and receives success or error messages
        String created = packageController.createPackage(builtPacakge);
        log.info("package creation : {}", created);

        if (created.equals(PackageController.SUCCESS)) {
            event.reply("Your package has been created successfully!").setEphemeral(true).queue();
        } else {
            event.reply("Your package was not created successfuly because " + created)
                    .setEphemeral(true)
                    .queue();
        }
    }

    /**
     * Check fields for nullability and build a valid package
     *
     * @param paramArray - extracted from event
     * @param userId
     * @return a built package object
     * @throws MissingMandatoryFieldsException
     */
    @VisibleForTesting
    Package buildPackage(String param, @Nonnull String userId) throws MissingMandatoryFieldsException {
        String[] paramArray = param.split("::");
        int size = paramArray.length;
        String packageName = size >= 1 ? paramArray[0] : "";
        String trackingNumber = size >= 2 ? paramArray[1] : "";
        String carrierId = size >= 3 ? paramArray[2] : "";

        // check null
        if (trackingNumber.isBlank() || carrierId.isBlank()) {
            log.error(
                    "Mandatory fields are missing - trackingNumber: {}, carrierId: {}",
                    trackingNumber,
                    carrierId);
            throw new MissingMandatoryFieldsException(String.format("trackingNumber: %s, carrierId: %s", trackingNumber, carrierId));
        }
        if (packageName.isBlank()) {
            log.info("The package name is null");
            packageName = null;
        }

        Package pkg =
                Package.builder()
                        .trackingNumber(trackingNumber)
                        .carrierId(carrierId)
                        .userId(userId)
                        .name(packageName)
                        .build();
        log.info(pkg.toString());

        return pkg;
    }
}
