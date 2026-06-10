package com.starfishst.bukkit.client;

import com.starfishst.bukkit.GuidoBukkitRuntime;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;

/** Extension for client */
public class BukkitClient extends Client {

  @NonNull private final GuidoBukkitRuntime runtime;

  public BukkitClient(
      @NonNull String token, @NonNull String ip, int port, @NonNull GuidoBukkitRuntime runtime) {
    super(token, ip, port);
    this.runtime = runtime;
  }

  @NonNull
  public BukkitClient startTask() {
    Time time = Time.of(30, Unit.SECONDS);
    runtime.getScheduler().repeat(time, time, new BukkitHeartBeatTimerTask(this));
    return this;
  }

  @Override
  public void onAuthentication(boolean authenticated) {}
}
