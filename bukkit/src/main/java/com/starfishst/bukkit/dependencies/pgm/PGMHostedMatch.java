package com.starfishst.bukkit.dependencies.pgm;

import com.starfishst.bukkit.matches.HostedMatch;
import com.starfishst.bukkit.matches.HostedPlayer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.teams.Team;

/** A match that is being hosted */
public class PGMHostedMatch extends HostedMatch {

  /**
   * The id of the pgm team and the id of the team of the match. The key is the id of the pgm team
   */
  @NonNull @Getter private final Map<String, MatchTeam> teams = new HashMap<>();
  /** The map which will be played in the match */
  // TODO both map and pgm match must be able to change
  @NonNull @Getter private final MapInfo map;
  /** The id of the pgm match */
  @NonNull @Getter private final String pgm;

  public PGMHostedMatch(
      @NonNull String id,
      @NonNull Set<HostedPlayer> participants,
      String ladder,
      @NonNull Map<String, Map<String, Object>> details,
      @NonNull MapInfo map,
      @NonNull String pgm) {
    super(id, participants, ladder, details);
    this.map = map;
    this.pgm = pgm;
  }

  public static Team getTeam(@NonNull Match match, @NonNull String id) {
    for (Party party : match.getParties()) {
      if (party instanceof Team && ((Team) party).getId().equals(id)) return (Team) party;
    }
    return null;
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
   * Get the id of the team that matches the pgm team
   *
   * @param pgmTeam the id of the pgm team
   * @return the id of the team that matches the pgm team
   */
  public int getTeamId(String pgmTeam) {
    if (pgmTeam == null) return -1;
    MatchTeam matchTeam = this.teams.get(pgmTeam);
    if (matchTeam != null) return matchTeam.getId();
    return -1;
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

  public Team getParty(@NonNull UUID uuid) {
    Match match = this.toPGM();
    if (match == null) return null;
    AtomicReference<Team> atomicParty = new AtomicReference<>();
    this.teams.forEach(
        (id, team) -> {
          Team pgmTeam = PGMHostedMatch.getTeam(match, id);
          for (TeamMember member : team.getMembers()) {
            if (member.getLink().getIdUUID("uuid", true).equals(uuid)) atomicParty.set(pgmTeam);
          }
        });
    return atomicParty.get();
  }

  public void setMap(@NonNull MapInfo info) {
    // TODO
  }

  @Override
  public String toString() {
    return "PGMHostedMatch{" + "teams=" + teams + ", map=" + map + ", pgm='" + pgm + '\'' + '}';
  }
}
