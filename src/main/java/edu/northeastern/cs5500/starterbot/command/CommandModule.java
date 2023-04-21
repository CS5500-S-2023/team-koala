package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public class CommandModule {

    @Provides
    @IntoSet
    public SlashCommandHandler provideSayCommand(SayCommand sayCommand) {
        return sayCommand;
    }

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
    public SlashCommandHandler providePreferredNameCommand(
            PreferredNameCommand preferredNameCommand) {
        return preferredNameCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler addReminderCommand(AddReminderCommand addReminderCommand) {
        return addReminderCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideButtonCommand(ButtonCommand buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideButtonCommandClickHandler(ButtonCommand buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideDropdownCommand(DropdownCommand dropdownCommand) {
        return dropdownCommand;
    }

    @Provides
    @IntoSet
    public StringSelectHandler provideDropdownCommandMenuHandler(DropdownCommand dropdownCommand) {
        return dropdownCommand;
    }
}
