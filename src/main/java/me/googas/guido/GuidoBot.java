package me.googas.guido;

import java.util.Arrays;
import java.util.Objects;
import javax.security.auth.login.LoginException;
import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.commands.jda.DefaultListenerOptions;
import me.googas.commands.jda.providers.registry.JdaProvidersRegistry;
import me.googas.guido.commands.EloCommands;
import me.googas.guido.commands.GuidoCommands;
import me.googas.guido.commands.providers.JdaArgumentProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class GuidoBot {

  private static JDA jda;
  private static final GuidoMessagesProvider messagesProvider = new GuidoMessagesProvider();
  private static CommandManager manager;

  public static void main(String[] args) throws LoginException, InterruptedException {
    GuidoConfig config = GuidoConfig.load();
    GuidoBot.jda =
        JDABuilder.create(config.getToken(), Arrays.asList(GatewayIntent.values()))
            .build()
            .awaitReady();
    JdaProvidersRegistry registry = new JdaProvidersRegistry(GuidoBot.messagesProvider);
    registry.addProvider(new JdaArgumentProvider());
    GuidoBot.manager =
        new CommandManager(
            registry,
            GuidoBot.messagesProvider,
            () -> GuidoBot.messagesProvider,
            GuidoBot.jda,
            new DefaultListenerOptions().setPrefix(config.getPrefix()));
    GuidoBot.manager.parseAndRegisterAll(new EloCommands(), new GuidoCommands());
  }

  @NonNull
  public static JDA getJda() {
    return Objects.requireNonNull(GuidoBot.jda, "JDA has not been initialized");
  }
}
