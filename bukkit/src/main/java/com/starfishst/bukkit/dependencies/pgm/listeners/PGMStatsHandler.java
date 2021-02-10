package com.starfishst.bukkit.dependencies.pgm.listeners;

import com.starfishst.bukkit.Guido;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.json.client.JsonClient;
import me.googas.starbox.modules.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;
import tc.oc.pgm.core.CoreLeakEvent;
import tc.oc.pgm.destroyable.DestroyableContribution;
import tc.oc.pgm.destroyable.DestroyableDestroyedEvent;
import tc.oc.pgm.flag.event.FlagCaptureEvent;
import tc.oc.pgm.goals.Contribution;
import tc.oc.pgm.wool.PlayerWoolPlaceEvent;

/** Track stats including pgm kills and deaths */
public class PGMStatsHandler implements Module {

  /** The map containing the stats of a player */
  @NonNull private final Map<UUID, Map<String, Float>> stats = new HashMap<>();

  /**
   * Listen to the death of a player to add the stat to the killer and the death
   *
   * @param event the event of a player dying
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(MatchPlayerDeathEvent event) {
    UUID victim = event.getVictim().getId();
    UUID killer = null;
    if (event.getKiller() != null) {
      killer = event.getKiller().getId();
    }
    String context = Guido.getConfiguration().getContext();
    this.increase(victim, context + "-deaths");
    if (killer != null) {
      this.increase(killer, context + "-kills");
    }
  }

  /**
   * Listen to a core being leaked
   *
   * @param event the event of a core being leaked
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onCoreLeakEvent(CoreLeakEvent event) {
    for (Contribution contribution : event.getCore().getContributions()) {
      this.increase(
          contribution.getPlayerState().getId(), Guido.getConfiguration().getContext() + "-cores");
    }
  }

  /**
   * Listen to a wool being placed
   *
   * @param event the event of a
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onWoolPlaced(PlayerWoolPlaceEvent event) {
    this.increase(event.getPlayer().getId(), Guido.getConfiguration().getContext() + "-wools");
  }

  /**
   * Listen to a monument being broken
   *
   * @param event the event of a monument being broken
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onMonumentBroke(DestroyableDestroyedEvent event) {
    for (DestroyableContribution contribution : event.getDestroyable().getContributions()) {
      this.increase(
          contribution.getPlayerState().getId(),
          Guido.getConfiguration().getContext() + "-monuments");
    }
  }

  /**
   * Listen to a flag being captured
   *
   * @param event the event of a flag being captured
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onFlagCaptureEvent(FlagCaptureEvent event) {
    this.increase(event.getCarrier().getId(), Guido.getConfiguration().getContext() + "-flags");
  }

  /**
   * Listen to when a match ends to save the stats
   *
   * @param event the event of the stat ending
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchEnd(MatchFinishEvent event) {
    String context = Guido.getConfiguration().getContext();
    for (MatchPlayer player : event.getMatch().getPlayers()) {
      if (!event.getMatch().getWinners().isEmpty()) {
        if (this.didWin(event, player)) {
          this.increase(player.getId(), context + "-wins");
        } else {
          if (this.isPlaying(event, player)) {
            this.increase(player.getId(), context + "-loses");
          }
        }
      } else {
        this.increase(player.getId(), context + "-ties");
      }
    }
    // Save them after we gave them the win and lose stats
    JsonClient connection = Guido.getClient().getConnection();
    if (connection != null) {
      this.stats.forEach(
          (uuid, statsMap) ->
              Requests.Links.saveStats(
                      new LinkableInfo(
                          LinkableType.MINECRAFT,
                          Maps.singleton("uuid", UUIDUtils.trim(uuid)),
                          new HashMap<>()),
                      statsMap)
                  .queue(connection));
    }
    this.stats.clear();
  }

  /**
   * Checks that the user queried is playing in the match that ended
   *
   * @param event the event of a match ending
   * @param player the player to check
   * @return true if the player was playing in the match that ended
   */
  private boolean isPlaying(@NonNull MatchFinishEvent event, @NonNull MatchPlayer player) {
    for (Party party : event.getMatch().getParties()) {
      if (party.isParticipating() && party.getPlayer(player.getId()) != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get whether a player won the match or not
   *
   * @param event the event of a match ending
   * @param player the player to getId if won or not
   * @return the result
   */
  private boolean didWin(@NonNull MatchFinishEvent event, @NonNull MatchPlayer player) {
    for (Competitor winner : event.getWinners()) {
      if (winner.getPlayers().contains(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the stats for an unique id
   *
   * @param uuid the unique id to getId the stats from
   * @return the stats of the player
   */
  @NonNull
  private Map<String, Float> getStats(@NonNull UUID uuid) {
    return this.stats.computeIfAbsent(uuid, k -> new HashMap<>());
  }

  /**
   * Increases a stat for a player by one
   *
   * @param uuid the unique id of the player to increase the stat to
   * @param key the key of the stat to increase
   */
  private void increase(@NonNull UUID uuid, @NonNull String key) {
    Map<String, Float> stats = this.getStats(uuid);
    stats.put(key, stats.getOrDefault(key, 0f) + 1);
  }

  @Override
  public void onDisable() {
    this.stats.clear();
  }

  /**
   * Get the name of this listener
   *
   * @return the name of the listener
   */
  @Override
  public @NonNull String getName() {
    return "pgm-stats";
  }
}
