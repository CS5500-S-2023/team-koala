package edu.northeastern.cs5500.starterbot.service.packageService;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.exception.KeyDeliveryException.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.PackageException.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

public class TrackPackageServiceTest {

    final String PACKAGE_NOT_FOUND_RESULT = "{\"code\": 60101, \"message\": \"no result found\"}";
    final String KEYDELIVERY_ERROR_RESULT = "{\"code\": 404, \"message\": \"Invalid API key\"}";

    final Package package1 =
            Package.builder()
                    .carrierId("ups")
                    .trackingNumber("1Z9A170W0337231977")
                    .userId("user id")
                    .build();
    // Invalid tracking number and carrier id
    final Package package2 =
            Package.builder()
                    .carrierId("fedex")
                    .trackingNumber("1Z9A170W0337231978")
                    .userId("user id")
                    .build();
    TrackPackageService trackPackageService;

    TrackPackageServiceTest() {
        // even though this is a singleton, it can still be tested like a normal class
        this.trackPackageService = new TrackPackageService(new InMemoryRepository<>());
    }

    @Test
    void testRealtimePackageTracking() throws KeyDeliveryCallException, PackageNotExistException {
        trackPackageService.getPackageLatestStatus(package1);
        assertEquals("[SEATTLE, WA, US]DELIVERED", package1.getStatus());
    }

    @Test
    void testReadDeliveryResponsePackgeNotExist() {
        assertThrows(
                "PACKAGE_NOT_FOUND",
                PackageNotExistException.class,
                () -> {
                    trackPackageService.readDeliveryResponse(PACKAGE_NOT_FOUND_RESULT, package2);
                });
    }

    @Test
    void testReadDeliveryResponseOtherKeyDeliveryError() {
        assertThrows(
                "KEYDELIVERY_ERROR",
                KeyDeliveryCallException.class,
                () -> {
                    trackPackageService.readDeliveryResponse(KEYDELIVERY_ERROR_RESULT, package1);
                });
    }
}
