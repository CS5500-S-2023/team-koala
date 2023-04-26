package edu.northeastern.cs5500.starterbot.service;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.Test;

public class GetPackageStatusSubTaskTest {
    private static final String namePkg = "first pacakge";
    private static final String statusPkg = "[SEATTLE, WA, US]DELIVERED";
    private static final Package package1 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("ups")
                    .userId("user id")
                    .name(namePkg)
                    .build();
    // Already have status
    private static final Package package2 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("ups")
                    .userId("user id")
                    .name(namePkg)
                    .status(statusPkg)
                    .build();

    GetPackageStatusSubTask getPackageStatusSubTask;

    public GetPackageStatusSubTaskTest() {
        GenericRepository repo = new InMemoryRepository<>();
        this.getPackageStatusSubTask =
                new GetPackageStatusSubTask(null, new TrackPackageService(repo), null, 0, 0, 0);
    }

    @Test
    public void testConstructMessage() {
        // Initial status is null

        String expectedString =
                String.format("The latest status for your package %s is %s", namePkg, statusPkg);
        assertThat(this.getPackageStatusSubTask.constructMessage(package1))
                .isEqualTo(expectedString);
    }

    @Test
    public void testConstructMessageNoUpdates() {
        assertThat(this.getPackageStatusSubTask.constructMessage(package2)).isEqualTo(null);
    }
}
