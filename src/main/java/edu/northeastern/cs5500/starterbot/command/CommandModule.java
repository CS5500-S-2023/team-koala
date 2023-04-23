package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.northeastern.cs5500.starterbot.command.PackageCommands.AddPackageCommand;
import edu.northeastern.cs5500.starterbot.command.PackageCommands.DeletePackageCommand;
import edu.northeastern.cs5500.starterbot.command.PackageCommands.DisplayPackagesCommand;
import edu.northeastern.cs5500.starterbot.command.PackageCommands.UpdatePackageCommand;
import lombok.Generated;

@Generated
@Module
public class CommandModule {

    @Provides
    @IntoSet
    public SlashCommandHandler provideAddPackageCommand(AddPackageCommand addPackageCommand) {
        return addPackageCommand;
    }

    @Provides
    @IntoSet
    public StringSelectHandler provideAddPackageCommandMenuHandler(
            AddPackageCommand addPackageCommand) {
        return addPackageCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideDeletePackageCommand(
            DeletePackageCommand deletePackageCommand) {
        return deletePackageCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideDisplayPackagesCommand(
            DisplayPackagesCommand displayPackagesCommand) {
        return displayPackagesCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideUpdatePackageCommand(
            UpdatePackageCommand updatePackageCommand) {
        return updatePackageCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler addReminderCommand(AddReminderCommand addReminderCommand) {
        return addReminderCommand;
    }
}
