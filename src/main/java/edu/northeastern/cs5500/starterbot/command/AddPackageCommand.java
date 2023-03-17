package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.model.Package;
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

        SelectMenu carrier =
                StringSelectMenu.create("string_select_add_package")
                        .addOption(
                                "Pizza", "pizza",
                                "Classic") // SelectOption with only the label, value, and
                        // description
                        .addOptions(
                                SelectOption.of(
                                                "Hamburger",
                                                "hamburger") // another way to create a SelectOption
                                        .withDescription("Tasty") // this time with a description
                                        .withDefault(true)) // while also being the default option
                        .build();

        event.reply("Select the carrier for your package").addActionRow(carrier).queue();
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
