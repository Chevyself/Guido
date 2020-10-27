package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.guido.api.data.implementations.data.MatchImpl;
import com.starfishst.guido.api.data.matches.Ladder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import me.googas.commons.RandomUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.cycle.CycleMatchModule;

/** Match makes */
public class MatchMakingListener implements GuidoListener {

  /** The id of the match that is being hosted */
  @Nullable private String matchId;

  @Receptor("can-host")
  public boolean canHost(@ParamName("match") MatchImpl match) {
    String type = match.getDetails().getValueOr("type", String.class, "none");
    String ladderName = match.getDetails().getValue("ladder", String.class);
    Match pgmMatch = this.getCurrentPgm();
    if (type.equalsIgnoreCase("pgm")
        && ladderName != null
        && (pgmMatch == null || !pgmMatch.isRunning())) {
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        try {
          Ladder ladder =
              connection.sendRequest(
                  new Request<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("name", ladderName)
                          .append("guild", match.getGuildId())
                          .build()));
          if (ladder != null) {
            return !this.getSuitableMaps(ladder).isEmpty();
          }
        } catch (MessengerListenFailException e) {
          e.printStackTrace();
          return false;
        }
      }
      // PGM.get().getMapLibrary().getMaps();
    }
    return false;
  }

  /**
   * get all the map that are suitable for a ladder
   *
   * @param ladder the ladder
   * @return the list of suitable maps
   */
  @NotNull
  public List<MapInfo> getSuitableMaps(@NotNull Ladder ladder) {
    List<MapInfo> suitableMaps = new ArrayList<>();
    Iterator<MapInfo> iterator = PGM.get().getMapLibrary().getMaps();
    while (iterator.hasNext()) {
      MapInfo map = iterator.next();
      Collection<Integer> maxPlayers = map.getMaxPlayers();
      if (maxPlayers.size() == 2) {
        int sum = 0;
        for (int maxPlayer : maxPlayers) {
          sum += maxPlayer;
        }
        if (sum == ladder.playersPerTeam() * 2) {
          suitableMaps.add(map);
        }
      }
    }
    return suitableMaps;
  }

  /**
   * Reads host requests
   *
   * @param match the match requesting to be hosted
   * @return the ip of the server if it can be hosted
   */
  @Receptor("host")
  public String host(@ParamName("match") MatchImpl match) {
    String type = match.getDetails().getValueOr("type", String.class, "none");
    String ladderName = match.getDetails().getValue("ladder", String.class);
    if (type.equalsIgnoreCase("PGM")) {
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        try {
          Ladder ladder =
              connection.sendRequest(
                  new Request<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("name", ladderName)
                          .append("guild", match.getGuildId())
                          .build()));
          if (ladder != null) {
            List<MapInfo> maps = this.getSuitableMaps(ladder);
            if (!maps.isEmpty()) {
              PGM.get().getMapOrder().setNextMap(RandomUtils.getRandom(maps));
              Match next = this.getCurrentPgm();
              if (next != null) {
                next.needModule(CycleMatchModule.class).cycleNow();
              }
              next = PGM.get().getMatchManager().getMatches().next();
              // Set teams
              Server server = Bukkit.getServer();
              return server.getIp() + ":" + server.getPort();
            }
          }
        } catch (MessengerListenFailException e) {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Get the current match that is being played by pgm
   *
   * @return the current match being played in pgm
   */
  @Nullable
  private Match getCurrentPgm() {
    Iterator<Match> matches = PGM.get().getMatchManager().getMatches();
    if (matches.hasNext()) {
      return matches.next();
    }
    return null;
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "match-making";
  }
}
