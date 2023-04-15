package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.awt.Color;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class DisplayPackagesCommand implements SlashCommandHandler {

    public static final String UNKNOWN = "Unknown";
    @Inject PackageController packageController;

    @Inject
    public DisplayPackagesCommand() {
        // Defined empty and public for dagger injection
    }

    @Nonnull
    public String getName() {
        return "display_packages";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "display all packages");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /display_packages");

        String userId = event.getUser().getId();
        List<Package> myPackages = packageController.getUsersPackages(userId);
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setColor(Color.red)
                        .setTitle("Displaying Your Packages")
                        .addBlankField(false);
        for (Package p : myPackages) {
            embedBuilder.addField("Package Id: ", displayPackageId(p.getId()), false);
            embedBuilder.addField("Carrier: ", displayCarrierId(p.getCarrierId()), true);
            embedBuilder.addField(
                    "Tracking Number: ", displayTrackingNumber(p.getTrackingNumber()), true);
            embedBuilder.addBlankField(true);
            embedBuilder.addField("Status: ", displayStatus(p.getStatus()), true);
            embedBuilder.addField("ETA: ", displayStatusTime(p.getStatusTime()), true);
            embedBuilder.addBlankField(true);
            embedBuilder.addBlankField(false);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Nonnull
    private String displayPackageId(@Nonnull ObjectId id) {
        return id.toString();
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
