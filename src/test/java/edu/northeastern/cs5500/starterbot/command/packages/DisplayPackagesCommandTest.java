package edu.northeastern.cs5500.starterbot.command.packages;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.controller.PackageController;
import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import edu.northeastern.cs5500.starterbot.service.packages.TrackPackageService;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class DisplayPackagesCommandTest {
    private PackageController packageController;
    private ObjectId objectId1 = new ObjectId("00000020f51bb4362eee2a4d");

    private Package package1 =
            Package.builder()
                    .trackingNumber("1Z9A170W0337231977")
                    .carrierId("ups")
                    .userId("user id")
                    .name("first pacakge")
                    .id(objectId1)
                    .build();

    DisplayPackagesCommand displayPackagesCommand;

    DisplayPackagesCommandTest() throws KeyDeliveryCallException, PackageNotExistException {
        this.displayPackagesCommand = new DisplayPackagesCommand();
        GenericRepository<Package> repo = new InMemoryRepository<>();
        this.packageController = new PackageController(repo, new TrackPackageService(repo));
        packageController.getPackageLatestStatus(package1);
    }

    @Test
    void testCreatePackageMessage() {
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.white);
        eb.addField("Package Id: ", "00000020f51bb4362eee2a4d", true);
        eb.addField("Package Name: ", "first pacakge", true);
        eb.addBlankField(true);
        eb.addField("Carrier: ", "ups", true);
        eb.addField("Tracking Number: ", "1Z9A170W0337231977", true);
        eb.addBlankField(true);
        eb.addField("Status: ", "[SEATTLE, WA, US]DELIVERED", true);
        eb.addField("ETA: ", "2023-02-24 18:50:18.0", true);
        eb.addBlankField(true);
        eb.addBlankField(false);
        assertThat(eb.build()).isEqualTo(displayPackagesCommand.createPackageMessage(package1));
    }
}
