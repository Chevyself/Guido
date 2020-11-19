package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.listeners.GroupListener;
import me.googas.api.client.Client;
import org.jetbrains.annotations.NotNull;

/** Extension for client */
public class BukkitClient extends Client {

  /**
   * Create the client
   *
   * @param token the token
   * @param ip the ip of the server of the bot
   * @param port the port of the server of the bot
   */
  public BukkitClient(@NotNull String token, @NotNull String ip, int port) {
    super(token, ip, port);
  }

  /**
   * Called when the client is authenticated
   *
   * @param authenticated whether the client was authenticated properly
   */
  @Override
  public void onAuthentication(boolean authenticated) {
    if (authenticated) {
      Guido.validated().requireListener(GroupListener.class).loadGroups(null);
    }
  }
}
