package com.starfishst.bungee.core.client;

import com.starfishst.bungee.api.Guido;
import lombok.NonNull;
import me.googas.api.client.HeartBeatTask;

/** This task makes the bungee client attempt to reconnect to the bot */
public class BungeeHeartBeatTimerTask implements HeartBeatTask {

  /** The client that is executing the task */
  @NonNull private final BungeeClient client;

  /**
   * Create the timer task
   *
   * @param client the client that is attempting to reconnect
   */
  public BungeeHeartBeatTimerTask(@NonNull BungeeClient client) {
    this.client = client;
  }

  @Override
  public @NonNull BungeeClient getClient() {
    return this.client;
  }

  @Override
  public void onSuccess() {
    Guido.getLogger().info("Client has connected with the bot");
  }

  @Override
  public void onError(@NonNull Throwable exception) {
    Guido.getLogger().severe("Could not connect with the bot attempting again");
  }
}
