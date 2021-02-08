package com.starfishst.bukkit.dependencies.events;

import dev.pgm.events.team.TournamentTeam;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.team.Team;
import me.googas.api.matches.team.TeamMember;

public class GuidoTournamentTeam implements TournamentTeam {

  @NonNull private final String name;

  @NonNull private final List<GuidoTournamentPlayer> players;

  public GuidoTournamentTeam(@NonNull String name, @NonNull List<GuidoTournamentPlayer> players) {
    this.name = name;
    this.players = players;
  }

  @NonNull
  public static GuidoTournamentTeam parse(@NonNull Team data) {
    List<GuidoTournamentPlayer> players = new ArrayList<>();
    for (TeamMember member : data.getMembers()) {
      UUID uuid = GuidoTournamentTeam.getUuid(member.getLink());
      if (uuid == null) continue;
      players.add(new GuidoTournamentPlayer(uuid, true));
    }
    return new GuidoTournamentTeam(data.getName(), players);
  }

  @Deprecated
  public static UUID getUuid(LinkableInfo info) {
    if (info == null) return null;
    if (info.getType() != LinkableType.MINECRAFT) return null;
    try {
      return info.getIdUUID("uuid", true);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public List<GuidoTournamentPlayer> getPlayers() {
    return this.players;
  }
}
