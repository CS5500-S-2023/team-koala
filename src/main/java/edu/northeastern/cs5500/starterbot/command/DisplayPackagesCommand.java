package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.awt.Color;
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
                new EmbedBuilder().setColor(Color.red).setTitle("Displaying Your Packages");
        for (Package p : myPackages) {
            embedBuilder.addField("Package Id: ", displayPackageId(p.getId()), true);
            embedBuilder.addField("Carrier: ", displayCarrierId(p.getCarrierId()), true);
            embedBuilder.addField(
                    "Tracking Number: ", displayTrackingNumber(p.getTrackingNumber()), true);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Nonnull
    private String displayPackageId(@Nonnull ObjectId id) {
        return id.toString();
    }

    @Nonnull
    private String displayCarrierId(@Nonnull String carrierId) {
        return carrierId;
    }

    @Nonnull
    private String displayTrackingNumber(@Nonnull String trackingNumber) {
        return trackingNumber;
    }
}
