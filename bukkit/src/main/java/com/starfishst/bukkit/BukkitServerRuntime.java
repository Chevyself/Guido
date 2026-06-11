package com.starfishst.bukkit;

import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.bukkit.GuidoConfiguration;
import java.io.File;
import java.io.InputStream;
import lombok.NonNull;
import me.googas.server.GuidoServerRuntime;
import me.googas.starbox.ProgramArguments;
import me.googas.starbox.events.ListenerManager;

public class BukkitServerRuntime implements GuidoServerRuntime {
  @NonNull private final GuidoBukkitRuntime bukkitRuntime;
  @NonNull private final GuidoConfiguration configuration;

  public BukkitServerRuntime(
      @NonNull GuidoBukkitRuntime bukkitRuntime, @NonNull GuidoConfiguration configuration) {
    this.bukkitRuntime = bukkitRuntime;
    this.configuration = configuration;
  }

  @Override
  public @NonNull ProgramArguments getArguments() {
    return ProgramArguments.construct(configuration.getBotArguments());
  }

  @Override
  public @NonNull File currentDirectory() {
    return bukkitRuntime.getPlugin().getDataFolder();
  }

  @Override
  public @NonNull InputStream getResource(@NonNull String name) {
    return bukkitRuntime.getPlugin().getResource(name);
  }

  @Override
  public @NonNull ListenerManager getListeners() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {
    // No-op
  }
}
