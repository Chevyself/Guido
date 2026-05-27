package com.starfishst.bukkit.client;

import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.starbox.Starbox;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;

/** Extension for client */
public class BukkitClient extends Client {

  public BukkitClient(@NonNull String token, @NonNull String ip, int port) {
    super(token, ip, port);
  }

  @NonNull
  public BukkitClient startTask() {
    Time time = Time.of(30, Unit.SECONDS);
    Starbox.getScheduler().repeat(time, time, new BukkitHeartBeatTimerTask(this));
    return this;
  }

  @Override
  public void onAuthentication(boolean authenticated) {
    if (authenticated) {
      // FIXME
      // Guido.getModuleRegistry().require(GroupsHandler.class).loadGroups(null);
    }
  }
}
