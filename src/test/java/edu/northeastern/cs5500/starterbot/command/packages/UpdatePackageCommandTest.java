package edu.northeastern.cs5500.starterbot.command.packages;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import org.junit.jupiter.api.Test;

public class UpdatePackageCommandTest {
    private UpdatePackageCommand updatePackageCommand;

    UpdatePackageCommandTest() throws KeyDeliveryCallException, PackageNotExistException {
        this.updatePackageCommand = new UpdatePackageCommand();
    }

    @Test
    void testGetName() {
        String returnedName = this.updatePackageCommand.getName();

        assertThat("update_package".equals(returnedName)).isTrue();
    }
}
