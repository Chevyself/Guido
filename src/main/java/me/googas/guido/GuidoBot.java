package me.googas.guido;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class GuidoBot {

    public static void main(String[] args) {
        GuidoConfig config = GuidoConfig.load();
        try {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(GatewayIntent.values())).build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
    }

}
