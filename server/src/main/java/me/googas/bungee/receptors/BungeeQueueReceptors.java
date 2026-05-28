package me.googas.bungee.receptors;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.bot.api.Guido;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

public class BungeeQueueReceptors {

  /** The uuids of the player in queue */
  @NonNull private final Set<UUID> inQueue = new HashSet<>();

  @Receptor(Requests.Bungee.ADD_QUEUE)
  public boolean addQueue(@ParamName("uuid") UUID uuid) {
    Guido.getLogger().info("Adding to queue " + uuid);
    return this.inQueue.add(uuid);
  }

  @Receptor(Requests.Bungee.REMOVE_QUEUE)
  public boolean removeQueue(@ParamName("uuid") UUID uuid) {
    Guido.getLogger().info("Removing from queue " + uuid);
    return this.inQueue.remove(uuid);
  }

  @EventHandler
  public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    UUID uniqueId = event.getPlayer().getUniqueId();
    if (this.inQueue.contains(uniqueId)) {
      // TODO there's no queue receptors yet
    }
  }
}
