package edu.northeastern.cs5500.starterbot.command.packages;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/** The command that allows the user to display all of the packages that belongs to the user */
@Singleton
@Slf4j
public class DisplayPackagesCommand implements SlashCommandHandler {

    public static final String UNKNOWN = "Unknown";
    @Inject PackageController packageController;

    @Inject
    public DisplayPackagesCommand() {
        // Defined empty and public for dagger injection
    }

    /**
     * Returns the name of the command
     *
     * @return String - the name of the command
     */
    @Nonnull
    public String getName() {
        return "display_packages";
    }

    /**
     * Returns the name and options of this command
     *
     * @return CommandData - the command information
     */
    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "display all packages");
    }

    /**
     * When the user interacts with this command, an event occurs. The command checks the event
     * input while parsing the data for the userId to determine which packages to display
     *
     * @param event - user's interaction with the command
     */
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /display_packages");

        String userId = event.getUser().getId();
        List<Package> myPackages = packageController.getUsersPackages(userId);
        EmbedBuilder embedBuilder =
                new EmbedBuilder().setColor(Color.red).setTitle("Displaying Your Packages");
        List<MessageEmbed> messages = new ArrayList<>();
        messages.add(embedBuilder.build());
        for (Package p : myPackages) {
            messages.add(createPackageMessage(p));
        }

        event.replyEmbeds(messages).queue();
    }

    /**
     * This method creates an embedded message for a package showing the packageId, name, carrierId,
     * tracking number, status, and ETA
     *
     * @param p of type Package, representing the package to display
     * @return MessageEmbed that contains all the information fields
     */
    @Nonnull
    protected MessageEmbed createPackageMessage(Package p) {
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white);
        eb.addField("Package Id: ", p.getId().toString(), true);
        eb.addField("Package Name: ", p.getName(), true);
        eb.addBlankField(true);
        eb.addField("Carrier: ", p.getCarrierId().toUpperCase(), true);
        eb.addField("Tracking Number: ", p.getTrackingNumber(), true);
        eb.addBlankField(true);
        eb.addField("Status: ", p.getStatus(), true);
        eb.addField("ETA: ", p.getStatusTime().toString(), true);
        eb.addBlankField(true);
        eb.addBlankField(false);
        return eb.build();
    }
}
