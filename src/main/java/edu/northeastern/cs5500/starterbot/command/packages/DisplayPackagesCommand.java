package edu.northeastern.cs5500.starterbot.command.packages;

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
        return eb.build();
    }

    /**
     * Returns the package Id as a string
     *
     * @param id ObjectId
     * @return id as a string
     */
    @Nonnull
    protected String displayPackageId(@Nonnull ObjectId id) {
        return id.toString();
    }

    /**
     * Returns the package name
     *
     * @param name String
     * @return name of the package
     */
    @Nonnull
    protected String displayPackageName(@Nonnull String name) {
        return name;
    }

    /**
     * Returns the carrier Id
     *
     * @param carrierId String
     * @return the package carrier
     */
    @Nonnull
    protected String displayCarrierId(@Nonnull String carrierId) {
        return carrierId.toUpperCase();
    }

    /**
     * Returns tracking number of the package
     *
     * @param trackingNumber String
     * @return the package trackingn number
     */
    @Nonnull
    protected String displayTrackingNumber(@Nonnull String trackingNumber) {
        return trackingNumber;
    }

    /**
     * Returns the status of the package
     *
     * @param status String
     * @return the status of the package
     */
    @Nonnull
    protected String displayStatus(String status) {
        return status;
    }

    /**
     * Returns the ETA of the package
     *
     * @param statusTime Date
     * @return the ETA as a String
     */
    @Nonnull
    protected String displayStatusTime(Date statusTime) {
        return statusTime.toString();
    }
}
