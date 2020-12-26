package me.googas.bot.core.server.receptors;

import me.googas.api.matches.team.Team;
import me.googas.bot.Guido;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class TeamDataReceptors {

  @Receptor("team")
  public Team team(@ParamName("id") String id) {
    return Guido.getDataLoader().getTeam(id);
  }

  @Receptor("team-by-name")
  public Team teamByName(@ParamName("name") String name) {
    return Guido.getDataLoader().getTeamByName(name);
  }
}
