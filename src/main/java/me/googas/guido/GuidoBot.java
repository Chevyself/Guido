package me.googas.guido;

import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.commands.jda.DefaultListenerOptions;
import me.googas.commands.jda.messages.MessagesProvider;
import me.googas.commands.jda.permissions.PermissionChecker;
import me.googas.commands.jda.providers.registry.JdaProvidersRegistry;
import me.googas.guido.commands.GuidoCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class GuidoBot {

    private static JDA jda;
    private static final GuidoMessagesProvider messagesProvider = new GuidoMessagesProvider();
    private static CommandManager manager;

    public static void main(String[] args) throws LoginException, InterruptedException {
        GuidoConfig config = GuidoConfig.load();
        GuidoBot.jda = JDABuilder.create(config.getToken(), Arrays.asList(GatewayIntent.values())).build().awaitReady();
        GuidoBot.manager = new CommandManager(new JdaProvidersRegistry(GuidoBot.messagesProvider), GuidoBot.messagesProvider, () -> GuidoBot.messagesProvider, GuidoBot.jda, new DefaultListenerOptions());
        GuidoBot.manager.parseAndRegister(new GuidoCommands());
    }

}
