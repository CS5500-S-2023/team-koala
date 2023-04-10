package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
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
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.selections.*;
import net.dv8tion.jda.api.interactions.components.text.*;
import net.dv8tion.jda.api.interactions.modals.*;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class DeletePackageCommand implements SlashCommandHandler {

    private final GenericRepository<Package> packages;

    @Inject
    public DeletePackageCommand(GenericRepository<Package> packages) {
        this.packages = packages;
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
                        "package id",
                        "The bot will delete the package associated with the package ID",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /delete_package");
        OptionMapping packageIdOption =
                Objects.requireNonNull(
                        event.getOption("package id"),
                        "Received null value for mandatory parameter 'tracking_number'");

        String packageId = packageIdOption.getAsString();
        ObjectId objectId = new ObjectId(packageId);
        packages.delete(objectId);

        event.reply("Your package has been deleted successfully");
    }
}
