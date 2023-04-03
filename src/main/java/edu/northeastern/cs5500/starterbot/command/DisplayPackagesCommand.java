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

        // Why is there an erorr trying to use package
        for (Package p : packages) {
            EmbedBuilder eb =
                    new EmbedBuilder()
                            .setColor(Color.LIGHT_GRAY)
                            .addField("Id: ", displayPackageId(p.getId()), false)
                            .addField("Name: ", displayPackageName(p.getName()), false)
                            .addField(
                                    "Tracking Number: ",
                                    displayTrackingNumber(p.getTrackingNumber()),
                                    true);
            // .addField(
            //         "ETA: ",
            //         displayEstimatedDeliveryDate(p.getEstimatedDeliveryDate()),
            //         true);
        }
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
