package edu.northeastern.cs5500.starterbot;

import dagger.Component;
import edu.northeastern.cs5500.starterbot.command.CommandModule;
import edu.northeastern.cs5500.starterbot.listener.MessageListener;
import edu.northeastern.cs5500.starterbot.repository.RepositoryModule;
import edu.northeastern.cs5500.starterbot.service.ServiceModule;
import edu.northeastern.cs5500.starterbot.service.GetPackageStatusTask;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

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
     *
     * @param - jda is needed to send messages to discord user
     */
    public void getPackageStatusDaily(JDA jda) {
        Timer timer = new Timer();
        TimerTask task = new GetPackageStatusTask(jda, trackPackageService);

        // The task is expected to run daily at noon of the day
        String startTime = "2023-04-17 12:00"; // By default, it is 24-hour clock
        String pattern = "yyyy-MM-dd hh:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date startDate;
        try {
            startDate = formatter.parse(startTime);
        } catch (ParseException e) {
            // This should never be reached as the input is valid and constant
            return;
        }
        long intervalInMilliseconds = TimeUnit.DAYS.toMillis(1);

        timer.schedule(task, startDate, intervalInMilliseconds);
    }
}
