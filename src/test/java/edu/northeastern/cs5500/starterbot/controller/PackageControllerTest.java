package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.exception.NotYourPackageException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import org.junit.jupiter.api.Test;

public class PackageControllerTest {
    private PackageController packageController;
    private Package package1 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("ups")
                    .userId("user id")
                    .name("first pacakge")
                    .build();

    private Package package2 =
            Package.builder()
                    .trackingNumber("1Z9X8Y670391444580")
                    .carrierId("ups")
                    .userId("user id")
                    .name("second package")
                    .build();

    private Package package3 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("fedex")
                    .userId("user id 2")
                    .name("third package")
                    .build();

    PackageControllerTest() {
        // Avoid using MongoDB service
        GenericRepository<Package> repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
    }

    @Test
    public void testCreatePackage() {
        assertEquals(PackageController.SUCCESS, packageController.createPackage(package1));
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
        packageController.deletePackage(package1.getId().toString(), package1.getUserId());
        assertThat(packageController.getUsersPackages(package1.getUserId()).size() == 1).isTrue();
    }

    @Test
    public void testUpdatePackage() throws IllegalArgumentException, NotYourPackageException {
        packageController.createPackage(package1);
        Package p =
                packageController.updatePackage(
                        package1.getId().toString(),
                        package1.getUserId(),
                        "new name",
                        "new tracking",
                        "new carrier");
        assertThat(p.getName()).isEqualTo("new name");
        assertThat(p.getTrackingNumber()).isEqualTo("new tracking");
        assertThat(p.getCarrierId()).isEqualTo("new carrier");
    }
}
