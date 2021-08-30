package me.googas.guido;

import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.commands.jda.DefaultListenerOptions;
import me.googas.commands.jda.providers.registry.JdaProvidersRegistry;
import me.googas.guido.commands.EloCommands;
import me.googas.guido.commands.FunCommands;
import me.googas.guido.commands.GuidoCommands;
import me.googas.guido.commands.providers.ActivityTypeProvider;
import me.googas.guido.commands.providers.JdaArgumentProvider;
import me.googas.guido.commands.providers.OnlineStatusProvider;
import me.googas.guido.config.GuidoConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class GuidoBot {

  @NonNull @Getter private static final GuidoConfig config = GuidoConfig.load();
  @NonNull public static Timer timer = new Timer();
  private static JDA jda;
  private static final GuidoMessagesProvider messagesProvider = new GuidoMessagesProvider();
  private static CommandManager manager;

  public static void main(String[] args) throws LoginException, InterruptedException {
    GuidoBot.jda =
        JDABuilder.create(GuidoBot.config.getToken(), Arrays.asList(GatewayIntent.values()))
            .build()
            .awaitReady();
    JdaProvidersRegistry registry = new JdaProvidersRegistry(GuidoBot.messagesProvider);
    // Experimental providers

    registry.addProvider(new ActivityTypeProvider());
    registry.addProvider(new JdaArgumentProvider());
    registry.addProvider(new OnlineStatusProvider());
    GuidoBot.manager =
        new CommandManager(
            registry,
            GuidoBot.messagesProvider,
            () -> GuidoBot.messagesProvider,
            GuidoBot.jda,
            new DefaultListenerOptions().setPrefix(GuidoBot.config.getPrefix()));
    GuidoBot.manager.parseAndRegisterAll(new EloCommands(), new FunCommands(), new GuidoCommands());
  }

  @NonNull
  public static JDA getJda() {
    return Objects.requireNonNull(GuidoBot.jda, "JDA has not been initialized");
  }
}
