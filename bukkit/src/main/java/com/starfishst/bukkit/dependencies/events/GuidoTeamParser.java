package com.starfishst.bukkit.dependencies.events;

import dev.pgm.events.Tournament;
import dev.pgm.events.team.TeamParser;
import dev.pgm.events.team.TournamentTeam;
import java.util.ArrayList;
import java.util.Collection;

public class GuidoTeamParser implements TeamParser {

  /** Enables the team parser to be used in the event plugin */
  public static void enable() {
    Tournament.get().setTeamParser(new GuidoTeamParser());
  }

  @Override
  public TournamentTeam getTeam(String s) {
    /* TODO teams requests have not been implemented
    if (s == null) return null;
    try {
      Team data = new BukkitRequest<>(Team.class, "team-by-name", Maps.singleton("name", s)).send();
      if (data != null) {
        return GuidoTournamentTeam.parse(data);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }

     */
    return null;
  }

  @Override
  public Collection<TournamentTeam> getTeams() {
    return new ArrayList<>();
  }
}
