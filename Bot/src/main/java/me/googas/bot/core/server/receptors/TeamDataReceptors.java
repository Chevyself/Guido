package me.googas.bot.core.server.receptors;

import me.googas.api.matches.TeamData;
import me.googas.bot.core.Guido;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class TeamDataReceptors {

  @Receptor("team")
  public TeamData team(@ParamName("id") String id) {
    return Guido.getDataLoader().getTeam(id);
  }

  @Receptor("team-by-name")
  public TeamData teamByName(@ParamName("name") String name) {
    return Guido.getDataLoader().getTeamByName(name);
  }
}
