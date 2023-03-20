package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.interactions.components.text.*;
import net.dv8tion.jda.api.interactions.modals.*;

@Singleton
@Slf4j
public class AddPackageCommand implements SlashCommandHandler, StringSelectHandler {

    private Package package1 = new Package();
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
                        OptionType.STRING, "package_alias", "The bot will record the name", false);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /add_package");

        // retrieve option data
        var aliasOption = event.getOption("package_alias");
        var trackingNumberOption = event.getOption("tracking_number");
        if (trackingNumberOption == null) {
            log.error("Received null value for mandatory parameter 'tracking_number'");
            return;
        }

        // set package data
        package1.reset(); // avoid newing several objects
        if (aliasOption != null) {
            package1.setName(aliasOption.getAsString());
        }
        String trackingNumber = trackingNumberOption.getAsString();
        package1.setTrackingNumber(trackingNumber);

        StringSelectMenu.Builder carrierBuilder =
                StringSelectMenu.create("string_select_add_package");
        for (String carrierKey : carrieMap.keySet()) {
            carrierBuilder.addOption(
                    carrierKey,
                    carrieMap.get(carrierKey),
                    "carrier name"); // label, value, description
        }

        event.reply("Select the carrier for your package")
                .addActionRow(carrierBuilder.build())
                .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        log.info("event: /string_select_add_package");
        String carrier = event.getValues().get(0);

        package1.setCarrierId(carrier);
        log.info(package1.toString());

        // create a package

        event.reply("Your package has been created successfully!").setEphemeral(true).queue();
    }
}