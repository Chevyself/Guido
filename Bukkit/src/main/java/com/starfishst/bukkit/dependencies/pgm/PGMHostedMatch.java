package com.starfishst.bukkit.dependencies.pgm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.starfishst.bukkit.matches.HostedMatch;
import com.starfishst.bukkit.matches.HostedPlayer;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.ValuesMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;

/** A match that is being hosted */
public class PGMHostedMatch extends HostedMatch {

  /**
   * The id of the pgm team and the id of the team of the match. The key is the id of the pgm team
   */
  @NonNull @Getter private final Map<String, Integer> teams = new HashMap<>();
  /** The map which will be played in the match */
  // TODO both map and pgm match must be able to change
  @NonNull @Getter private final MapInfo map;
  /** The id of the pgm match */
  @NonNull @Getter private final String pgm;

  public PGMHostedMatch(@NonNull String id, @NonNull Set<HostedPlayer> participants, @Nullable String ladder, @NonNull ValuesMap details, @NonNull MapInfo map, @NonNull String pgm) {
    super(id, participants, ladder, details);
    this.map = map;
    this.pgm = pgm;
  }

  /**
   * Get the id of the team that matches the pgm team
   *
   * @param pgmTeam the id of the pgm team
   * @return the id of the team that matches the pgm team
   */
  public int getTeamId(String pgmTeam) {
    return pgmTeam == null ? -1 : this.teams.getOrDefault(pgmTeam, -1);
  }

  /**
   * Check whether a command sender is participating in a match
   *
   * @param sender the sender to check
   * @return true if the sender is participating
   */
  public boolean isParticipating(@NonNull CommandSender sender) {
    if (!(sender instanceof Player)) return false;
    for (HostedPlayer participant : this.getParticipants()) {
      if (participant.getUniqueId().equals(((Player) sender).getUniqueId())) return true;
    }
    return false;
  }

  /**
   * Get the pgm match that is hosting this match
   *
   * @return the match if the id matches null otherwise
   */
  public Match toPGM() {
    Iterator<Match> matches = PGM.get().getMatchManager().getMatches();
    while (matches.hasNext()) {
      Match next = matches.next();
      if (next.getId().equals(this.pgm)) return next;
    }
    return null;
  }
}
