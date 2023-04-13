package edu.northeastern.cs5500.starterbot.service;

import java.util.Collection;
import java.util.Objects;
import java.util.TimerTask;
import net.dv8tion.jda.api.JDA;
import lombok.extern.slf4j.Slf4j;
import edu.northeastern.cs5500.starterbot.model.Package;

@Slf4j
public class GetPackageStatusTask extends TimerTask {
    private JDA jda;
    TrackPackageService trackPackageService;

    public GetPackageStatusTask(JDA jda, TrackPackageService trackPackageService) {
        this.jda = jda;
        this.trackPackageService = trackPackageService;
    }

    @Override
    public void run() {
        // retrieve all packages
        Collection<Package> allPackages = trackPackageService.packageRepository.getAll();

        // get their status and compare with the existing status
        for (Package pkg: allPackages) {
            log.info("Getting status of " + pkg);
            String currStatus = pkg.getStatus();
            trackPackageService.getPackageLatestStatus(pkg);
            String latestStatus = pkg.getStatus();

            // current status could be null
            if (Objects.equals(currStatus, latestStatus)) continue;

            // TODO: deciding on what information to show the user
            sendMessage(pkg.getUserId(), String.format("The latest status for your package %s is %s", pkg.getName(), pkg.getStatus()));
        }
    }

    public void sendMessage(String userId, String content) {
        log.info("Sending message to user "+ userId +" : " + content);
        jda.retrieveUserById(userId).queue(user -> {
                user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(content))
                    .queue();
            });
        
    }
}
