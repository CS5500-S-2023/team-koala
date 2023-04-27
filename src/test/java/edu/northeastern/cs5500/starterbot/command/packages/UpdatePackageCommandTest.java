package edu.northeastern.cs5500.starterbot.command.packages;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import edu.northeastern.cs5500.starterbot.service.packages.TrackPackageService;
import org.junit.jupiter.api.Test;

public class UpdatePackageCommandTest {
    private PackageController packageController;
    UpdatePackageCommand updatePackageCommand;

    UpdatePackageCommandTest() throws KeyDeliveryCallException, PackageNotExistException {
        this.updatePackageCommand = new UpdatePackageCommand();
        GenericRepository<Package> repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
    }

    @Test
    void testGetName() {
        String returnedName = this.updatePackageCommand.getName();

        assertThat("update_package".equals(returnedName)).isTrue();
    }
}
