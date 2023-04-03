package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.awt.Color;
import java.util.ArrayList;
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
public class DisplayPackagesCommand {

    private final ArrayList<Package> packages;

    @Inject
    public DisplayPackagesCommand(GenericRepository<Package> packageRepository) {
        this.packages = new ArrayList<>(packageRepository.getAll());
    }

    @Nonnull
    public String getName() {
        return "display_packages";
    }

    public CommandData getCommandData() {
        return Commands.slash(getName(), "display all packages");
    }

    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /display_packages");

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

    private String displayPackageId(ObjectId id) {
        return id.toString();
    }

    private String displayPackageName(String name) {
        return name;
    }

    private String displayTrackingNumber(String trackingNumber) {
        return trackingNumber;
    }

    // private String displayEstimatedDeliveryDate(Date estimatedDeliveryDate) {
    //     DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    //     String strEstimatedDeliveryDate = dateFormat.format(estimatedDeliveryDate);
    //     return strEstimatedDeliveryDate;
    // }
}
