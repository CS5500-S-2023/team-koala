package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.sql.Timestamp;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class UpdatePackageCommand implements SlashCommandHandler {

    @Inject PackageController packageController;

    @Inject
    public UpdatePackageCommand() {
        // Defined empty and public for dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "update_package";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Update a package")
                .addOption(
                        OptionType.STRING,
                        "package_id",
                        "The bot will update the package associated with the package ID",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /update_package");
        OptionMapping packageIdOption =
                Objects.requireNonNull(
                        event.getOption("package_id"),
                        "Received null value for mandatory parameter 'package_id'");

        OptionMapping nameOption = event.getOption("name");
        OptionMapping trackingNumberOption = event.getOption("tracking_number");
        OptionMapping carrierIdOption = event.getOption("carrier");
        OptionMapping statusOption = event.getOption("status");
        OptionMapping statusTimeOption = event.getOption("status_time");

        String packageId = packageIdOption.getAsString();
        ObjectId objectId = new ObjectId(packageId);
        Package p = packageController.getPackage(objectId);

        String name = nameOption.getAsString();
        String trackingNumber = trackingNumberOption.getAsString();
        String carrierId = carrierIdOption.getAsString();
        String status = statusOption.getAsString();
        Timestamp statusTime = Timestamp.valueOf(statusTimeOption.getAsString());

        if (name == null) name = p.getName();
        if (trackingNumber == null) trackingNumber = p.getTrackingNumber();
        if (carrierId == null) carrierId = p.getCarrierId();
        if (status == null) status = p.getStatus();
        if (statusTime == null) statusTime = p.getStatusTime();

        packageController.updatePackage(
                objectId, name, trackingNumber, carrierId, status, statusTime);

        event.reply("Your package has been updated successfully").queue();
    }
}
