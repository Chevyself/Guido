package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import lombok.NonNull;
import me.googas.api.client.tasks.HeartBeatTask;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitHeartBeatTimerTask extends BukkitRunnable implements HeartBeatTask {

  @NonNull private final BukkitClient client;

  public BukkitHeartBeatTimerTask(@NonNull BukkitClient client) {
    this.client = client;
  }

  @Override
  public @NonNull BukkitClient getClient() {
    return this.client;
  }

  @Override
  public void onSuccess() {
    Guido.getPlugin().getLogger().info("Client has connected with the bot");
  }

  @Override
  public void onError(@NonNull Throwable exception) {
    Guido.getPlugin().getLogger().severe("Could not connect with the bot attempting again");
  }
}
