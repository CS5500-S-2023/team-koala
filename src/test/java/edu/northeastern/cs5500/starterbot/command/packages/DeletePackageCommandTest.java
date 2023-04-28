package edu.northeastern.cs5500.starterbot.command.packages;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import org.junit.jupiter.api.Test;

public class DeletePackageCommandTest {
    private DeletePackageCommand deletePackageCommand;

    DeletePackageCommandTest() throws KeyDeliveryCallException, PackageNotExistException {
        this.deletePackageCommand = new DeletePackageCommand();
    }

    @Test
    void testGetName() {
        String returnedName = this.deletePackageCommand.getName();

        assertThat("delete_package".equals(returnedName)).isTrue();
    }
}
