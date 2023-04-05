package edu.northeastern.cs5500.starterbot.Service;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.service.TrackPackageService;

public class TrackPackageServiceTest {
    TrackPackageService trackPackageService;

    TrackPackageServiceTest() {
        // even though this is a singleton, it can still be tested like a normal class
        this.trackPackageService = new TrackPackageService();
    }

    // @Test
    // void testcreatePackageTracking() {
    //     Package package1 = new Package();
    //     package1.setCarrierId("ups");
    //     package1.setTrackingNumber("1Z9A170W0337231977");

    //     assertEquals(trackPackageService.SUCCESS,
    //         trackPackageService.createPackageTracking(package1));
    // }
}
