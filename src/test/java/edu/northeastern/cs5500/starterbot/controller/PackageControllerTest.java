package edu.northeastern.cs5500.starterbot.controller;

import static org.junit.Assert.*;

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
            new Package(null, "second package", "trackingNumber2", "1Z9A170W0337231977", "ups");
    private Package package3 =
            new Package(null, "third package", "trackingNumber2", "1Z9A170W0337231977", "notUPS");

    PackageControllerTest() {
        // Avoid using MongoDB service
        GenericRepository repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
    }

    @Test
    public void testCreatePackage() {
        assertEquals(packageController.createPackage(package1), packageController.SUCCESS);
    }

    @Test
    public void testGetUsersPackages() {
        packageController.createPackage(package1);
        packageController.createPackage(package2);
        packageController.createPackage(package3);
        assertEquals(packageController.getUsersPackages(package1.getUserId()).size(), 2);
    }

    @Test
    public void testDeletePackage() {
        packageController.createPackage(package1);
        packageController.createPackage(package2);
        packageController.deletePackage(package1.getId());
        assertEquals(packageController.getUsersPackages(package1.getUserId()).size(), 1);
    }
}
