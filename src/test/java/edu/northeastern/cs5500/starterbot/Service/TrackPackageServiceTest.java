package edu.northeastern.cs5500.starterbot.Service;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import org.junit.jupiter.api.Test;

public class TrackPackageServiceTest {
    TrackPackageService trackPackageService;

    TrackPackageServiceTest() {
        // even though this is a singleton, it can still be tested like a normal class
        this.trackPackageService = new TrackPackageService();
    }

    @Test
    void testRealtimePackageTracking() {
        Package package1 = new Package();
        package1.setCarrierId("ups");
        package1.setTrackingNumber("1Z9A170W0337231977");

        package1 = trackPackageService.getPackageLatestStatus(package1);
        assertEquals("[SEATTLE, WA, US]DELIVERED", package1.getStatus());
    }
}
