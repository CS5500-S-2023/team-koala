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

    PackageControllerTest() {
        // Avoid using MongoDB service
        GenericRepository repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
    }

    @Test
    public void testCreatePackage() {
        assertEquals(PackageController.SUCCESS, packageController.createPackage(package1));
    }
}
