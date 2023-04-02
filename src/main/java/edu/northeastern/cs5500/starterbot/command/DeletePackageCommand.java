import edu.northeastern.cs5500.starterbot.repository.GenericRepository;

public class DeletePackageCommand implements SlashCommandHandler, StringSelectHandler {

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

        try {
            packages.delete(packageId);
        } catch (PackageDoesNotExistException e) {
            e.printStackTrace();
            throw e;
        }

        event.reply("Your package has been deleted successfully");
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        log.info("event: /string_select_delete_package");
        String packageId = event.getValues().get(0);

        packages.delete(packageId);
        log.info(packages.toString());

        event.reply("Your package has been deleted successfully!");
    }
}
