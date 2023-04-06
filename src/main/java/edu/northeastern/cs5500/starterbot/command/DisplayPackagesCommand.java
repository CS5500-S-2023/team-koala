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
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.interactions.components.text.*;
import net.dv8tion.jda.api.interactions.modals.*;
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

        List<Package> packages = packageController.getAllPackages();

        EmbedBuilder embedBuilder =
                new EmbedBuilder().setColor(Color.LIGHT_GRAY).setTitle("Displaying Packages");
        for (Package p : packages) {
            embedBuilder.addField("Id: ", displayPackageId(p.getId()), true);
            embedBuilder.addField("Name: ", displayPackageName(p.getName()), true);
            embedBuilder.addField(
                    "Tracking Number: ", displayTrackingNumber(p.getTrackingNumber()), true);
            // .addField(
            //         "ETA: ",
            //         displayEstimatedDeliveryDate(p.getEstimatedDeliveryDate()),
            //         true);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Nonnull
    private String displayPackageId(@Nonnull ObjectId id) {
        return id.toString();
    }

    @Nonnull
    private String displayPackageName(@Nonnull String name) {
        return name;
    }

    @Nonnull
    private String displayTrackingNumber(@Nonnull String trackingNumber) {
        return trackingNumber;
    }

    // private String displayEstimatedDeliveryDate(Date estimatedDeliveryDate) {
    //     DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    //     String strEstimatedDeliveryDate = dateFormat.format(estimatedDeliveryDate);
    //     return strEstimatedDeliveryDate;
    // }
}
