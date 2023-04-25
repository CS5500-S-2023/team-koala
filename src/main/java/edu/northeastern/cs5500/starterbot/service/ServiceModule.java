package edu.northeastern.cs5500.starterbot.service;

import dagger.Module;
import dagger.Provides;
import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Module
@Slf4j
public abstract class ServiceModule { // NOSONAR

    static String getBotToken() {
        return new ProcessBuilder().environment().get("BOT_TOKEN");
    }

    @Provides
    @Singleton
    static JDA provideJDA() {
        String token = getBotToken();
        if (token == null) {
            throw new IllegalArgumentException(
                    "The BOT_TOKEN environment variable is not defined.");
        }
        @SuppressWarnings("null")
        @Nonnull
        Collection<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
        return JDABuilder.createLight(token, intents).build();
    }
}
