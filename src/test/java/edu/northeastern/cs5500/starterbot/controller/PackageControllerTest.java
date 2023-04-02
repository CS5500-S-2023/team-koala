package edu.northeastern.cs5500.starterbot.controller;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

public class PackageControllerTest {
    private PackageController packageController;
    private Package package1 =
            new Package(null, "first pacakge", "firstPackage", "1Z9A170W0337231977", "ups");

    PackageControllerTest() {
        // Avoid using MongoDB service
        this.packageController = new PackageController(new InMemoryRepository<>());
    }

    @Test
    public void testCreatePackage() {
        assertEquals(packageController.createPackage(package1), packageController.SUCCESS);
    }
}
