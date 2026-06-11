package dev.xevy.bukkit.client;

import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.client.tasks.HeartBeatTask;
import me.googas.starbox.logging.LoggerFactory;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitHeartBeatTimerTask extends BukkitRunnable implements HeartBeatTask {

  @NonNull private final Logger logger = LoggerFactory.getLogger(BukkitHeartBeatTimerTask.class);
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
    logger.info("Client has connected with the bot");
  }

  @Override
  public void onError(@NonNull Throwable exception) {
    logger.severe("Could not connect with the bot attempting again");
  }
}
