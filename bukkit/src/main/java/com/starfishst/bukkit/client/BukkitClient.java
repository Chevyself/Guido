package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.modules.GroupsHandler;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.starbox.Starbox;

/** Extension for client */
public class BukkitClient extends Client {

  public BukkitClient(@NonNull String token, @NonNull String ip, int port) {
    super(token, ip, port);
  }

  @NonNull
  public BukkitClient startTask() {
    Time time = new Time(30, Unit.SECONDS);
    Starbox.getScheduler().repeat(time, time, new BukkitHeartBeatTimerTask(this));
    return this;
  }

  @Override
  public void onAuthentication(boolean authenticated) {
    if (authenticated) {
      Guido.getModuleRegistry().require(GroupsHandler.class).loadGroups(null);
    }
  }
}
