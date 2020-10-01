package com.starfishst.guido.pgm.configuration;

import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.cache.ICatchable;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.guido.pgm.GuidoPlugin;
import com.starfishst.guido.pgm.api.config.DataLoader;
import com.starfishst.guido.pgm.api.config.PlayerData;
import com.starfishst.guido.pgm.api.events.GuidoListener;
import com.starfishst.guido.pgm.api.events.config.PlayerDataUnloadedEvent;
import com.starfishst.utils.gson.GsonProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/** The data loader implementation for guido */
public class GuidoDataLoader implements DataLoader, GuidoListener {

  /** The plugin required to load the file */
  @NotNull private final GuidoPlugin plugin;

  /**
   * Create the guido data loader
   *
   * @param plugin the plugin required to load data
   */
  public GuidoDataLoader(@NotNull GuidoPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Save the data of a player when it is unloaded
   *
   * @param event the event of the data being unloaded
   */
  @EventHandler
  public void onPlayerDataUnloaded(PlayerDataUnloadedEvent event) {
    File file;
    try {
      file =
          CoreFiles.getOrCreate(
              plugin.getDataFolder() + "/database/" + event.getData().getUniqueId() + ".json");
    } catch (IOException e) {
      Fallback.addError(
          "IOException in GuidoDataLoader: The data could not be saved for "
              + event.getData().getUniqueId());
      e.printStackTrace();
      file = null;
    }
    if (file != null) {
      try {
        FileWriter writer = new FileWriter(file);
        GsonProvider.GSON.toJson(event.getData(), writer);
        writer.close();
      } catch (IOException e) {
        Fallback.addError(
            "IOException in GuidoDataLoader: The writer failed to get the file for "
                + event.getData().getUniqueId());
        e.printStackTrace();
      }
    }
  }

  @Override
  public @NotNull String getName() {
    return "data-loader";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void onUnload() {
    for (ICatchable catchable : Cache.getCache()) {
      if (catchable instanceof PlayerData) {
        this.onPlayerDataUnloaded(new PlayerDataUnloadedEvent((PlayerData) catchable));
      }
    }
  }

  @Override
  public @NotNull PlayerData getPlayerData(@NotNull UUID uniqueId) {
    PlayerData data =
        Cache.getCatchable(
            catchable ->
                catchable instanceof GuidoPlayer
                    && ((GuidoPlayer) catchable).getUniqueId().equals(uniqueId),
            GuidoPlayer.class);
    if (data != null) {
      return data;
    } else {
      File file = CoreFiles.getFile(plugin.getDataFolder() + "/database/" + uniqueId + ".json");
      if (file != null) {
        try {
          FileReader reader = new FileReader(file);
          return GsonProvider.GSON.fromJson(reader, GuidoPlayer.class);
        } catch (FileNotFoundException e) {
          Fallback.addError("IOException in GuidoDataLoader: " + file + " was not found!");
          e.printStackTrace();
        }
      }
      return new GuidoPlayer(uniqueId, new ArrayList<>());
    }
  }
}
