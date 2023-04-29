package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.northeastern.cs5500.starterbot.command.packages.AddPackageCommand;
import edu.northeastern.cs5500.starterbot.command.packages.DeletePackageCommand;
import edu.northeastern.cs5500.starterbot.command.packages.DisplayPackagesCommand;
import edu.northeastern.cs5500.starterbot.command.packages.UpdatePackageCommand;
import edu.northeastern.cs5500.starterbot.command.reminders.AddReminderCommand;
import edu.northeastern.cs5500.starterbot.command.reminders.DeleteAllRemindersCommand;

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

    @Provides
    @IntoSet
    public SlashCommandHandler deleteAllReminderCommand(
            DeleteAllRemindersCommand deleteAllReminderCommand) {
        return deleteAllReminderCommand;
    }
}
