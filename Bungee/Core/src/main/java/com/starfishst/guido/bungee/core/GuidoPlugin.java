package com.starfishst.guido.bungee.core;

import com.starfishst.bungee.CommandManager;
import com.starfishst.bungee.messages.DefaultMessagesProvider;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.providers.registry.ProvidersRegistry;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.guido.bungee.api.Guido;
import com.starfishst.guido.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.guido.bungee.api.configuration.GuidoServer;
import com.starfishst.guido.bungee.api.events.GuidoListener;
import com.starfishst.guido.bungee.core.commands.GuidoCommands;
import com.starfishst.guido.bungee.core.configuration.GuidoBungeeConfiguration;
import com.starfishst.guido.bungee.core.listeners.JoinListener;
import com.starfishst.guido.bungee.core.listeners.MotdListener;
import com.starfishst.guido.implementations.Implementation;
import com.starfishst.guido.implementations.ImplementationClient;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/** The guido plugin for Bungee */
public class GuidoPlugin extends Plugin implements Implementation {

  /** The command manager */
  @NotNull
  private final CommandManager manager =
      new CommandManager(this, new DefaultMessagesProvider(), new ProvidersRegistry<>());

  /** The bungeeConfiguration that the plugin will use */
  @NotNull private BungeeConfiguration bungeeConfiguration = new GuidoBungeeConfiguration();

  /** The listeners being used by the plugin */
  @NotNull
  private final List<GuidoListener> listeners = Lots.list(new JoinListener(), new MotdListener());

  /** The client connected with the bot */
  @NotNull private final ImplementationClient client = new ImplementationClient("0");

  /** Loads the configuration */
  public void loadConfiguration() {
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

  /** Loads the servers that can be connected */
  public void loadServers() {
    ProxyServer proxy = this.getProxy();
    for (GuidoServer server : this.bungeeConfiguration.getServers()) {
      if (proxy.getServerInfo(server.getName()) == null) {
        proxy
            .getServers()
            .put(
                server.getName(),
                proxy.constructServerInfo(
                    server.getName(), server.constructAddress(), "Ignored", server.isRestricted()));
      }
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
    this.client.setToken(this.bungeeConfiguration.getToken());
    for (GuidoListener listener : this.listeners) {
      listener.register(this);
    }
    manager.registerCommand(new GuidoCommands());
    super.onEnable();
  }

  @Override
  public @NotNull ImplementationClient getClient() {
    return this.client;
  }
}
