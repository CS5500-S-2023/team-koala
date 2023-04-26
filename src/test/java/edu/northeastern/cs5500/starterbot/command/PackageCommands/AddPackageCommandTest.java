package edu.northeastern.cs5500.starterbot.command.PackageCommands;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import edu.northeastern.cs5500.starterbot.exception.MissingMandatoryFieldsException;
import edu.northeastern.cs5500.starterbot.model.Package;
import org.junit.jupiter.api.Test;

public class AddPackageCommandTest {
    final String name = "first package";
    final String trackingNumber = "1Z9A170W0337231977";
    final String carrierId = "ups";
    final String userId = "945507823498965002";

    final String valid_paramArray = String.format("%s::%s::%s", name, trackingNumber, carrierId);
    final String missing_pkgName_paramArray = String.format("::%s::%s", trackingNumber, carrierId);
    final String missing_trackingNumber_paramArray = String.format("%s::::%s", name, carrierId);
    final String missing_carrierId_paramArray = String.format("%s::%s::", name, trackingNumber);

    AddPackageCommand addPackageCommand;

    AddPackageCommandTest() {
        this.addPackageCommand = new AddPackageCommand();
    }

    @Test
    void testBuildPackageAllValidValues() throws MissingMandatoryFieldsException {
        Package returnedVal = this.addPackageCommand.buildPackage(valid_paramArray, userId);
        Package expectedVal =
                Package.builder()
                        .carrierId(carrierId)
                        .trackingNumber(trackingNumber)
                        .userId(userId)
                        .name(name)
                        .build();
        assertThat(returnedVal.equals(expectedVal)).isTrue();
    }

    @Test
    void testBuildPackageMissingPackageName() throws MissingMandatoryFieldsException {
        Package returnedVal =
                this.addPackageCommand.buildPackage(missing_pkgName_paramArray, userId);
        Package expectedVal =
                Package.builder()
                        .carrierId(carrierId)
                        .trackingNumber(trackingNumber)
                        .userId(userId)
                        .build();
        assertThat(returnedVal.equals(expectedVal)).isTrue();
    }

    @Test
    void testBuildPackageMissingTrackingNumber() {
        assertThrows(
                "missing_trackingNumber",
                MissingMandatoryFieldsException.class,
                () -> {
                    this.addPackageCommand.buildPackage(missing_trackingNumber_paramArray, userId);
                });
    }

    @Test
    void testBuildPackageMissingCarrierId() {
        assertThrows(
                "missing_carrierId",
                MissingMandatoryFieldsException.class,
                () -> {
                    this.addPackageCommand.buildPackage(missing_carrierId_paramArray, userId);
                });
    }

    @Test
    void testGetName() {
        String returnedName = this.addPackageCommand.getName();

        assertThat("add_package".equals(returnedName)).isTrue();
    }
}
