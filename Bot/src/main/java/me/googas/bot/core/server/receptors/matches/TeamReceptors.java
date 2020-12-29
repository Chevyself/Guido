package me.googas.bot.core.server.receptors.matches;

import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.matches.team.Team;
import me.googas.api.matches.team.TeamMember;
import me.googas.bot.Guido;
import me.googas.bot.core.matches.team.GuidoTeam;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class TeamReceptors {

  @Receptor("team/create")
  public Team create(
      @ParamName("name") String name, @ParamName("members") Set<TeamMember> members) {
    return new GuidoTeam(name, members);
  }

  @Receptor("team/add")
  public boolean add(@ParamName("id") String id, @ParamName("member") TeamMember member) {
    Team team = this.getTeam(id);
    if (team == null) return false;
    return team.add(member);
  }

  @Receptor("team/remove")
  public boolean remove(@ParamName("id") String id, @ParamName("member") TeamMember member) {
    Team team = this.getTeam(id);
    if (team == null) return false;
    return team.remove(member);
  }

  @Nullable
  public Team getTeam(@NonNull String id) {
    return Guido.getDataLoader().getTeam(id);
  }
}
