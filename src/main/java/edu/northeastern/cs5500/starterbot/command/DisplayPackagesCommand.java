package edu.northeastern.cs5500.starterbot.command;

import java.awt.Color;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.interactions.components.text.*;
import net.dv8tion.jda.api.interactions.modals.*;
import java.util.Date;


@Singleton
@Slf4j
public class DisplayPackagesCommand {

    private final GenericRepository<Package> packages;
    
    @Inject
    public DisplayPackagesCommand(GenericRepository<Package> packages) {
        this.packages = packages;
    }

    @Override
    @Nonnull
    public String getName() {
        return "display_packages";
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), "display all packages");
    }

    //
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /display_packages");

        // Why is there an erorr trying to use package
        for (Package package : packages) {
            EmbedBuilder eb = new EmbedBuilder()
            .setColor(Color.LIGHT_GRAY)
            .addField("Id: ", displayPackageId(package.getId()), false)
            .addField("Name: ", displayPackageName(package.getName()), false)
            .addField("Tracking Number: ", displayTrackingNumber(package.getTrackingNumber()), true)
            .addField("ETA: ", displayEstimatedDeliveryDate(package.getEstimatedDeliveryDate()), true);

            event.reply(eb.build());
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

    private Date displayEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        return estimatedDeliveryDate;
    }
}