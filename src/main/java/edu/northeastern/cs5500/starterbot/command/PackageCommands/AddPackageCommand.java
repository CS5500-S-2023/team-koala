package edu.northeastern.cs5500.starterbot.command.PackageCommands;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.command.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
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

    // may have a better solution to read the data from csv file
    // https://github.com/CS5500-S-2023/team-koala/issues/15
    private final Map<String, String> carrieMap =
            Map.of(
                    "UPS", "ups",
                    "DHL", "dhl",
                    "FedEx", "fedex",
                    "USPS", "usps",
                    "LaserShip", "lasership",
                    "China-post", "cpcbe",
                    "China Ems International", "china_ems_international",
                    "GLS", "gls",
                    "Canada Post", "canada_post",
                    "Purolator", "purolator");

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
        for (Map.Entry<String, String> entry : carrieMap.entrySet()) {
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
        String[] paramArray = event.getValues().get(0).split("::");
        String packageName = paramArray[0];
        String trackingNumber = paramArray[1];
        String carrierId = paramArray[2];

        // retrieve user info
        User user = event.getUser();
        if (packageName.isBlank()) {
            log.info("The package name is null");
            packageName = null;
        }

        Package pkg =
                Package.builder()
                        .trackingNumber(trackingNumber)
                        .carrierId(carrierId)
                        .userId(user.getId())
                        .name(packageName)
                        .build();
        log.info(pkg.toString());

        // create a package and receives success or error messages
        String created = packageController.createPackage(pkg);
        log.info("package creation : " + created);

        if (created.equals(PackageController.SUCCESS)) {
            event.reply("Your package has been created successfully!").setEphemeral(true).queue();
        } else {
            event.reply("Your package was not created successfuly because " + created)
                    .setEphemeral(true)
                    .queue();
        }
    }
}
