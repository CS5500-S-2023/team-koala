package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import java.util.Objects;
import java.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

/*
 * This is a task which is scheduled to send the updated status of all packages
 */
@Slf4j
public class GetPackageStatusTask extends TimerTask {
    @Inject JDA jda;
    @Inject TrackPackageService trackPackageService;

    @Inject
    public GetPackageStatusTask(JDA jda, TrackPackageService trackPackageService) {
        this.jda = jda;
        this.trackPackageService = trackPackageService;
    }

    /**
     * Send the updated status of all packages in our database to users
     *
     * <p>1. Get all packages from the MongoDB database 2. Get the latest status of packages via
     * third-party API 3. If the status of a package doesn't have updates, no update will be sent to
     * the user. 4. Send a private message to users of packages about latest status
     */
    @Override
    public void run() {
        // retrieve all packages from the database
        Collection<Package> allPackages = trackPackageService.packageRepository.getAll();
        Map<String, StringBuilder> packageStatusMessages = new HashMap<>();

        log.info("Getting status of all packages");
        // get their status and compare with the existing status
        for (Package pkg : allPackages) {
            String currStatus = pkg.getStatus();
            trackPackageService.getPackageLatestStatus(pkg);
            String latestStatus = pkg.getStatus();

            // current status could be null
            if (Objects.equals(currStatus, latestStatus)) continue;

            // Display package's name (if not set, display tracking number)
            // and the latest status
            String packageIdentifier =
                    Objects.equals(pkg.getName(), null) ? pkg.getTrackingNumber() : pkg.getName();
            String statusMessage =
                    String.format(
                            "The latest status for your package %s is %s",
                            packageIdentifier, pkg.getStatus());

            packageStatusMessages.putIfAbsent(pkg.getUserId(), new StringBuilder());
            packageStatusMessages.get(pkg.getUserId()).append(statusMessage + "\n");
        }

        log.info("Sending messages of package status updates to users");
        for (Entry<String, StringBuilder> entry : packageStatusMessages.entrySet()) {
            sendMessage(entry.getKey(), entry.getValue().toString());
        }
    }

    /**
     * Send the latest status of all packages for a user in discord private channel
     *
     * @param userId - discord user id
     * @param content - a long message string which contains the updates
     */
    private void sendMessage(String userId, String content) {

        jda.retrieveUserById(userId)
                .queue(
                        user -> {
                            user.openPrivateChannel()
                                    .flatMap(channel -> channel.sendMessage(content))
                                    .queue();
                        });
        log.info("Package status updates have been sent to discord user " + userId);
    }
}
