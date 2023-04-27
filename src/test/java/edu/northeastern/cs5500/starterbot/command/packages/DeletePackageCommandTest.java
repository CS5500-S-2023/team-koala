package edu.northeastern.cs5500.starterbot.command.packages;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import edu.northeastern.cs5500.starterbot.service.packages.TrackPackageService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class DeletePackageCommandTest {
    private PackageController packageController;
    private ObjectId objectId1 = new ObjectId("00000020f51bb4362eee2a4d");
    private ObjectId objectId2 = new ObjectId("00000020f51bb4362eee2a4b");
    private Package package1 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("ups")
                    .userId("user id")
                    .name("first pacakge")
                    .id(objectId1)
                    .build();
    private Package package2 =
            Package.builder()
                    .trackingNumber("1Z9X8Y670391444580")
                    .carrierId("ups")
                    .userId("user id")
                    .name("second package")
                    .id(objectId2)
                    .build();

    DeletePackageCommand deletePackageCommand;

    DeletePackageCommandTest() throws KeyDeliveryCallException, PackageNotExistException {
        this.deletePackageCommand = new DeletePackageCommand();
        GenericRepository<Package> repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
        packageController.getPackageLatestStatus(package1);
        packageController.createPackage(package1);
    }

    @Test
    void testGetName() {
        String returnedName = this.deletePackageCommand.getName();

        assertThat("delete_package".equals(returnedName)).isTrue();
    }
}
