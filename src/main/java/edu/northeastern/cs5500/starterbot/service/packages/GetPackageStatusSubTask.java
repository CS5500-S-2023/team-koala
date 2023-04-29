package edu.northeastern.cs5500.starterbot.service.packages;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

/**
 * This class is a timer task that get package latest status and notify discord users in a private
 * discord channel when there are updates
 */
@Slf4j
public class GetPackageStatusSubTask extends TimerTask {
    JDA jda;
    TrackPackageService trackPackageService;
    int startIdx;
    int endIdx;
    int taskId;
    Package[] allPackages;

    /**
     * Public constructor for usage in GetPackageStatusTask which is an overarching task composing
     * of multiple sub-tasks
     *
     * @param jda - represents a connection to discord
     * @param trackPackageService - TrackPackageService
     * @param allPackages - all packages in database as an array
     * @param startIdx - the starting index in the allPackages array(inclusive)
     * @param endIdx - the ending index in the allPackages array(exclusive)
     * @param taskId - task number
     */
    public GetPackageStatusSubTask(
            JDA jda,
            TrackPackageService trackPackageService,
            Package[] allPackages,
            int startIdx,
            int endIdx,
            int taskId) {
        this.jda = jda;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.taskId = taskId;
        this.allPackages = allPackages;
        this.trackPackageService = trackPackageService;
    }

    /** Send the updated status of ranged packages to users */
    @Override
    public void run() {
        if (startIdx >= endIdx) {
            log.info("No more packages for task {} with start index {}", taskId, startIdx);
            return;
        }
        // Store packages and their status update message
        Map<String, StringBuilder> packageStatusMessages = new HashMap<>();

        Date currTime = new Date();
        log.info(
                "Getting status of {} - {} packages for task {} at time {}",
                startIdx,
                endIdx,
                taskId,
                currTime);

        // Get package status and compare with the existing status
        for (int i = startIdx; i < endIdx; i++) {
            Package pkg = allPackages[i];

            String statusMessage = constructMessage(pkg);
            // null means no updates
            if (statusMessage == null) continue;

            // Update in database
            trackPackageService.packageRepository.update(pkg);

            packageStatusMessages.putIfAbsent(pkg.getUserId(), new StringBuilder());
            packageStatusMessages.get(pkg.getUserId()).append(statusMessage + "\n");
        }

        log.info("Sending messages of package status updates to users");
        for (Entry<String, StringBuilder> entry : packageStatusMessages.entrySet()) {
            sendMessage(entry.getKey(), entry.getValue().toString());
        }
    }

    /**
     * Contruct a message of updated status for this package
     *
     * @param pkg - provided package object
     * @return null if no updates; a message string if having updates
     */
    @VisibleForTesting
    String constructMessage(Package pkg) {
        String currStatus = pkg.getStatus();
        // All packages should be valid
        try {
            trackPackageService.getPackageLatestStatus(pkg);
        } catch (Exception e) {
            log.error(
                    "constructMessage: Something wrong happened as verification checks in adding/update packages fail",
                    e);
            return null;
        }

        String latestStatus = pkg.getStatus();

        // current status could be null
        if (Objects.equals(currStatus, latestStatus)) {
            return null;
        }

        // construct the message to sent
        String packageIdentifier =
                Objects.equals(pkg.getName(), null) ? pkg.getTrackingNumber() : pkg.getName();

        return String.format(
                "The latest status for your package %s is %s", packageIdentifier, pkg.getStatus());
    }

    /**
     * Send the latest status of all packages for a user in discord private channel
     *
     * @param userId - discord user id
     * @param content - a long message string which contains the updates
     */
    private void sendMessage(String userId, String content) {

        jda.retrieveUserById(userId)
                .submit()
                .whenCompleteAsync(
                        (user, error) -> {
                            if (error != null) {
                                user.openPrivateChannel()
                                        .flatMap(channel -> channel.sendMessage(content))
                                        .complete();
                            } else {
                                log.error(
                                        "This user {} may have block our bot or hasn't enable private messages",
                                        userId,
                                        error);
                            }
                        });
        log.info("Package status updates have been sent to discord user " + userId);
    }
}
