package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.listeners.GroupsListener;
import java.io.IOException;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.json.client.JsonClient;

/** Extension for client */
public class BukkitClient extends Client {

  /**
   * Create the client
   *
   * @param token the token
   * @param ip the ip of the server of the bot
   * @param port the port of the server of the bot
   */
  public BukkitClient(@NonNull String token, @NonNull String ip, int port) {
    super(token, ip, port);
  }

  /**
   * Connects the client with the bot
   *
   * @return the stabilised connection
   * @throws IOException if the bot cannot be reached
   */
  @Override
  public @NonNull JsonClient startConnection() throws IOException {
    new BukkitHeartBeatTimerTask(this)
        .runTaskTimer(
            Guido.validated(), 20, new Time(30, Unit.SECONDS).getValue(Unit.MINECRAFT_TICKS));
    return super.startConnection();
  }

  /**
   * Called when the client is authenticated
   *
   * @param authenticated whether the client was authenticated properly
   */
  @Override
  public void onAuthentication(boolean authenticated) {
    if (authenticated) {
      Guido.validated().requireListener(GroupsListener.class).loadGroups(null);
    }
  }
}
