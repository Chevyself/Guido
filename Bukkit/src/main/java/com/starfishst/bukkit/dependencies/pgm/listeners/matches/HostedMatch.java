package com.starfishst.bukkit.dependencies.pgm.listeners.matches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;

/** A match that is being hosted */
public class HostedMatch {

  /** The id of the match that is being hosted */
  @NonNull private final String id;

  /** The ladder which is being played */
  @NonNull private final String ladder;

  /** The participants that are gonna play the match */
  @NonNull private final Collection<LinkableInfo> participants;

  /**
   * The id of the pgm team and the id of the team of the match. The key is the id of the pgm team
   */
  @NonNull private final Map<String, Integer> teams = new HashMap<>();
  /** The details of the match */
  @NonNull private final ValuesMap details;
  /** The map which will be played in the match */
  @NonNull private final MapInfo map;
  /** The id of the pgm match */
  @NonNull private final String pgmId;

  /**
   * Create the match to host
   *
   * @param id the id of the match to host
   * @param ladder the ladder in which the match is being played
   * @param participants the participants of the match
   * @param map the map in which
   * @param pgmId the id of the pgm match
   * @param details the details of the match
   */
  public HostedMatch(
      @NonNull String id,
      @NonNull String ladder,
      @NonNull Collection<LinkableInfo> participants,
      @NonNull MapInfo map,
      @NonNull String pgmId,
      @NonNull ValuesMap details) {
    this.id = id;
    this.ladder = ladder;
    this.participants = participants;
    this.map = map;
    this.pgmId = pgmId;
    this.details = details;
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
    if (!(sender instanceof Player)) return true;
    SimpleLinkableInfo senderInfo =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT,
            new SimpleValuesMap("uuid", UUIDUtils.trim(((Player) sender).getUniqueId())));
    for (LinkableInfo participant : this.getParticipants()) {
      if (senderInfo.compare(participant)) return true;
    }
    return false;
  }

  /**
   * Get the pgm match that is hosting this match
   *
   * @return the match if the id matches null otherwise
   */
  public Match getPgm() {
    Iterator<Match> matches = PGM.get().getMatchManager().getMatches();
    while (matches.hasNext()) {
      Match next = matches.next();
      if (next.getId().equals(this.pgmId)) return next;
    }
    return null;
  }

  /**
   * Get the id of the pgm match that is hosting this match
   *
   * @return the id of the pgm match
   */
  @NonNull
  public String getPgmId() {
    return this.pgmId;
  }

  /**
   * Get the details of the match
   *
   * @return the map that contains the details of the match
   */
  @NonNull
  public ValuesMap getDetails() {
    return this.details;
  }

  /**
   * Get the id of the match that is being hosted
   *
   * @return the id of the hosted match
   */
  @NonNull
  public String getId() {
    return this.id;
  }

  /**
   * Get a copy as a list of the participants of the match
   *
   * @return a copy of the participants of the match
   */
  @NonNull
  public List<LinkableInfo> getParticipants() {
    return new ArrayList<>(this.participants);
  }

  /**
   * Get the map of teams playing the match
   *
   * @return the map of teams
   */
  @NonNull
  public Map<String, Integer> getTeams() {
    return this.teams;
  }
}
