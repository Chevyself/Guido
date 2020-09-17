package com.starfishst.guido.bungee.core;

import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.guido.bungee.api.Guido;
import com.starfishst.guido.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.guido.bungee.core.configuration.GuidoBungeeConfiguration;
import com.starfishst.guido.bungee.core.listeners.MotdListener;
import java.io.File;
import java.io.IOException;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/** The guido plugin for Bungee */
public class GuidoPlugin extends Plugin {

  /** The bungeeConfiguration that the plugin will use */
  @NotNull private BungeeConfiguration bungeeConfiguration = new GuidoBungeeConfiguration();

  /** Loads the configuration */
  private void loadConfiguration() {
    File dataFolder = this.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }
    try {
      File file =
          CoreFiles.getFileOrResource(
              dataFolder.getPath() + "/config.yml", this.getResourceAsStream("config.yml"));
      this.bungeeConfiguration =
          new GuidoBungeeConfiguration(
              ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
    } catch (IOException e) {
      Fallback.addError("IOException: config.yml could not be loaded");
      e.printStackTrace();
    }
  }

  /**
   * Get the bungeeConfiguration for the guido plugin
   *
   * @return the bungeeConfiguration for the guido plugin
   */
  @NotNull
  public BungeeConfiguration getBungeeConfiguration() {
    return bungeeConfiguration;
  }

  @Override
  public void onEnable() {
    Guido.setPlugin(this);
    this.loadConfiguration();
    new MotdListener().register(this);
    super.onEnable();
  }
}
