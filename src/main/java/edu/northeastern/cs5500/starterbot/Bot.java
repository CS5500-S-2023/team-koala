package edu.northeastern.cs5500.starterbot;

import dagger.Component;
import edu.northeastern.cs5500.starterbot.command.CommandModule;
import edu.northeastern.cs5500.starterbot.listener.MessageListener;
import edu.northeastern.cs5500.starterbot.repository.RepositoryModule;
import edu.northeastern.cs5500.starterbot.service.ServiceModule;
import edu.northeastern.cs5500.starterbot.service.GetPackageStatusTask;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import java.util.Timer;
import java.util.TimerTask;

@Component(modules = {CommandModule.class, RepositoryModule.class, ServiceModule.class})
@Singleton
interface BotComponent {
    public Bot bot();
}

public class Bot {

    @Inject
    Bot() {}

    @Inject MessageListener messageListener;
    @Inject JDA jda;
    @Inject TrackPackageService trackPackageService;

    static String getBotToken() {
        return new ProcessBuilder().environment().get("BOT_TOKEN");
    }

    void start() {

        jda.addEventListener(messageListener);
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(messageListener.allCommandData());
        commands.queue();

        getPackageStatusDaily(jda);
    }

    /**
     * Only notify users when there are updates for the package
     */
    public void getPackageStatusDaily(JDA jda) {
        Timer timer = new Timer();
        TimerTask task = new GetPackageStatusTask(jda, trackPackageService);
        timer.schedule(task, 2000, 100000);
    }
}
