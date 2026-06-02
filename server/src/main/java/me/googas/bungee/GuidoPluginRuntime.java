package me.googas.bungee;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import lombok.NonNull;
import me.googas.bungee.configuration.BungeeConfiguration;
import me.googas.server.GuidoRuntime;
import me.googas.starbox.ProgramArguments;

public class GuidoPluginRuntime implements GuidoRuntime {

  @NonNull private final GuidoPlugin plugin;
  @NonNull private final BungeeConfiguration configuration;

  public GuidoPluginRuntime(
      @NonNull GuidoPlugin plugin, @NonNull BungeeConfiguration configuration) {
    this.plugin = plugin;
    this.configuration = configuration;
  }

  @Override
  public @NonNull ProgramArguments getArguments() {
    return ProgramArguments.construct(this.configuration.getBotArguments());
  }

  @Override
  public @NonNull File currentDirectory() {
    return plugin.getDataFolder();
  }

  @Override
  public @NonNull InputStream getResource(@NonNull String name) {
    return Objects.requireNonNull(
        plugin.getResourceAsStream(name), "Could not find resource " + name);
  }
}
