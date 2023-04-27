package edu.northeastern.cs5500.starterbot.command.PackageCommands;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
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
import org.bson.types.ObjectId;

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
            EmbedBuilder eb = new EmbedBuilder().setColor(Color.white);
            eb.addField("Package Id: ", displayPackageId(p.getId()), true);
            eb.addField("Package Name: ", displayPackageName(p.getName()), true);
            eb.addBlankField(true);
            eb.addField("Carrier: ", displayCarrierId(p.getCarrierId()), true);
            eb.addField("Tracking Number: ", displayTrackingNumber(p.getTrackingNumber()), true);
            eb.addBlankField(true);
            eb.addField("Status: ", displayStatus(p.getStatus()), true);
            eb.addField("ETA: ", displayStatusTime(p.getStatusTime()), true);
            eb.addBlankField(true);
            eb.addBlankField(false);
            messages.add(eb.build());
            // embedBuilders.add(eb);
        }

        event.replyEmbeds(messages).queue();
    }

    @Nonnull
    private String displayPackageId(@Nonnull ObjectId id) {
        return id.toString();
    }

    @Nonnull
    private String displayPackageName(@Nonnull String name) {
        if (name == null) return UNKNOWN;
        return name;
    }

    @Nonnull
    private String displayCarrierId(@Nonnull String carrierId) {
        return carrierId.toUpperCase();
    }

    @Nonnull
    private String displayTrackingNumber(@Nonnull String trackingNumber) {
        return trackingNumber;
    }

    @Nonnull
    private String displayStatus(String status) {
        if (status == null) return UNKNOWN;
        return status;
    }

    @Nonnull
    private String displayStatusTime(Date statusTime) {
        if (statusTime == null) return UNKNOWN;
        return statusTime.toString();
    }
}
