package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.handlers.GroupsHandler;
import java.io.IOException;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.json.client.JsonClient;

/** Extension for client */
public class BukkitClient extends Client {

  /** Whether the task is registered */
  private boolean task = false;

  public BukkitClient(@NonNull String token, @NonNull String ip, int port) {
    super(token, ip, port);
  }

  @Override
  public @NonNull JsonClient startConnection() throws IOException {
    if (!this.task) {
      Time time = new Time(30, Unit.SECONDS);
      Guido.getScheduler().repeat(time, time, new BukkitHeartBeatTimerTask(this));
      this.task = true;
    }
    return super.startConnection();
  }

  @Override
  public void onAuthentication(boolean authenticated) {
    if (authenticated) {
      Guido.getHandlerRegistry().requireHandler(GroupsHandler.class).loadGroups(null);
    }
  }
}
