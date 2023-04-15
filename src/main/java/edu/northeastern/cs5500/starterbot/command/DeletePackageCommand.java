package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
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
public class DeletePackageCommand implements SlashCommandHandler {

    @Inject PackageController packageController;

    @Inject
    public DeletePackageCommand() {
        // Defined empty and public for dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "delete_package";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Delete a package")
                .addOption(
                        OptionType.STRING,
                        "package_id",
                        "The bot will delete the package associated with the package ID",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /delete_package");
        OptionMapping packageIdOption =
                Objects.requireNonNull(
                        event.getOption("package_id"),
                        "Received null value for mandatory parameter 'package_id'");

        String userId = event.getUser().getId();
        String packageId = packageIdOption.getAsString();
        ObjectId objectId = new ObjectId(packageId);

        Boolean deleted = packageController.deletePackage(objectId, userId);

        if (deleted) {
            event.reply("Your package has been deleted successfully").queue();
        } else {
            event.reply("This package does not exist").queue();
        }
    }
}
