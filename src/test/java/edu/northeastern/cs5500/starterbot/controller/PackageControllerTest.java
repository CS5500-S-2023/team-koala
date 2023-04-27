package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.exception.packageException.NotYourPackageException;
import edu.northeastern.cs5500.starterbot.exception.packageException.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import edu.northeastern.cs5500.starterbot.service.packageService.TrackPackageService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class PackageControllerTest {
    private PackageController packageController;
    private ObjectId objectId1 = new ObjectId("00000020f51bb4362eee2a4d");
    private ObjectId objectId2 = new ObjectId("00000020f51bb4362eee2a4b");
    private ObjectId objectId3 = new ObjectId("00000020f51bb4362eee2a4c");
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

    private Package package3 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("fedex")
                    .userId("user id 2")
                    .name("third package")
                    .id(objectId3)
                    .build();

    PackageControllerTest() {
        GenericRepository<Package> repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
    }

    @Test
    public void testCreatePackageValid() {
        assertThat(PackageController.SUCCESS.equals(packageController.createPackage(package2)))
                .isTrue();
    }

    @Test
    public void testCreatePackageExistentPackage() {
        packageController.createPackage(package1);
        assertThat(
                        PackageController.PACKAGE_ALREADY_EXISTS_MESSAGE.equals(
                                packageController.createPackage(package1)))
                .isTrue();
    }

    @Test
    public void testGetPackage() {
        packageController.createPackage(package1);
        assertThat(packageController.getPackage(package1.getId().toString())).isEqualTo(package1);
        assertThrows(
                IllegalArgumentException.class, () -> packageController.getPackage("badObjectId"));
    }

    @Test
    public void testGetUsersPackages() {
        packageController.createPackage(package1);
        packageController.createPackage(package2);
        packageController.createPackage(package3);
        assertThat(packageController.getUsersPackages(package1.getUserId()).size() == 2).isTrue();
    }

    @Test
    public void testDeletePackage() throws IllegalArgumentException, NotYourPackageException {
        packageController.createPackage(package1);
        packageController.createPackage(package2);
        packageController.createPackage(package3);
        assertThrows(
                IllegalArgumentException.class,
                () -> packageController.deletePackage("bad Id", package2.getUserId()));
        packageController.deletePackage(package1.getId().toString(), package1.getUserId());
        assertThat(packageController.getUsersPackages(package1.getUserId()).size() == 1).isTrue();
    }

    @Test
    public void testUpdatePackage()
            throws IllegalArgumentException, NotYourPackageException, PackageNotExistException {
        packageController.createPackage(package1);
        assertThrows(
                PackageNotExistException.class,
                () ->
                        packageController.updatePackage(
                                package1.getId().toString(),
                                package1.getUserId(),
                                package1.getName(),
                                package1.getTrackingNumber(),
                                "fedex"));
        assertThrows(
                NotYourPackageException.class,
                () ->
                        packageController.updatePackage(
                                package1.getId().toString(),
                                package3.getUserId(),
                                "name3",
                                "tracking3",
                                "carrier3"));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        packageController.updatePackage(
                                "badId", package3.getUserId(), "name3", "tracking3", "carrier3"));
        package1 =
                packageController.updatePackage(
                        package1.getId().toString(),
                        package1.getUserId(),
                        "new name",
                        "1Z9X8Y670391444580",
                        "ups");
        assertThat(package1.getName()).isEqualTo("new name");
        assertThat(package1.getTrackingNumber()).isEqualTo("1Z9X8Y670391444580");
        assertThat(package1.getCarrierId()).isEqualTo("ups");
    }
}
