package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import lombok.NonNull;
import me.googas.api.client.HeartBeatTask;
import org.bukkit.scheduler.BukkitRunnable;

/** This task makes the bukkit client attempt to reconnect to the bot */
public class BukkitHeartBeatTimerTask extends BukkitRunnable implements HeartBeatTask {

  /** The client that is executing the task */
  @NonNull private final BukkitClient client;

  /**
   * Create the timer task
   *
   * @param client the client that is attempting to reconnect
   */
  public BukkitHeartBeatTimerTask(@NonNull BukkitClient client) {
    this.client = client;
  }

  @Override
  public @NonNull BukkitClient getClient() {
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
