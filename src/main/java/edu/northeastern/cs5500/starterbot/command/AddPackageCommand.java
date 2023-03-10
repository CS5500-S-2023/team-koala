package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.text.*;
import net.dv8tion.jda.api.interactions.modals.*;

@Singleton
@Slf4j
public class AddPackageCommand implements SlashCommandHandler, ModalListener {

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
        return Commands.slash(getName(), "Add a new package");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /add_package");
        TextInput name =
                TextInput.create("name", "Name", TextInputStyle.SHORT)
                        .setPlaceholder("Name of this package")
                        .setMaxLength(100)
                        .setRequired(false)
                        .build();

        // This field is required and usually between 8 to 40 characters
        TextInput trackingNumber =
                TextInput.create("trackingNumber", "Tracking Number", TextInputStyle.SHORT)
                        .setPlaceholder("Your complete tracking number")
                        .setMaxLength(50)
                        .build();

        Modal modal =
                Modal.create("mod_add_package", "Add Package")
                        .addActionRow(trackingNumber)
                        .addActionRow(name)
                        .build();

        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        String name = event.getValue("name").getAsString();
        String trackingNumber = event.getValue("trackingNumber").getAsString();

        // create a package

        event.reply("Your package has been created successfully!").setEphemeral(true).queue();
    }
}
