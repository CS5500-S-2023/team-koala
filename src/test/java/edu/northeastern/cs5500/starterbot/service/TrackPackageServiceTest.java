package edu.northeastern.cs5500.starterbot.service;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

public class TrackPackageServiceTest {
    TrackPackageService trackPackageService;

    TrackPackageServiceTest() {
        // even though this is a singleton, it can still be tested like a normal class
        this.trackPackageService = new TrackPackageService(new InMemoryRepository<>());
    }

    @Test
    void testRealtimePackageTracking() {
        Package package1 =
                Package.builder()
                        .carrierId("ups")
                        .trackingNumber("1Z9A170W0337231977")
                        .userId("user id")
                        .build();

        trackPackageService.getPackageLatestStatus(package1);
        assertEquals("[SEATTLE, WA, US]DELIVERED", package1.getStatus());
    }
}
