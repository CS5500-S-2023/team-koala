package edu.northeastern.cs5500.starterbot.command.PackageCommands;

import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.NotYourPackageException;
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

        try {
            packageController.deletePackage(packageId, userId);
        } catch (NotYourPackageException e) {
            event.reply(e.getMessage()).queue();
        } catch (IllegalArgumentException e) {
            event.reply("This is not a valid package id!");
        }

        event.reply("Your package has been deleted successfully").queue();
    }
}
