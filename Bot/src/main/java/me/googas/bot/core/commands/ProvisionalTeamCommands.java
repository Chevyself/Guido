package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.TeamData;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.bot.api.types.BotLinkable;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoTeamData;
import me.googas.bot.core.types.GuidoTeamMember;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.Lots;
import me.googas.commons.Strings;

/** @deprecated this commands are provisional to create teams for this tournament only */
public class ProvisionalTeamCommands {

  @Parent
  @Command(aliases = "team", description = "Parent team commands")
  public Result team(
      @Required(
              name = "team",
              description = "The name or the id of the team. If the name has spaces use `_`")
          TeamData team) {
    StringBuilder builder = Strings.getBuilder();
    builder.append("**").append(team.getName()).append("**");
    for (TeamMember member : team.getMembers()) {
      if (member.getTeamRole() == TeamRole.LEADER) {
        builder
            .append("\nLeader: ")
            .append("`")
            .append(member.getLinkInfo().getSingle())
            .append("`");
      }
    }
    for (TeamMember member : team.getMembers()) {
      if (member.getTeamRole() == TeamRole.NORMAL) {
        builder.append("\n- ").append("`").append(member.getLinkInfo().getSingle()).append("`");
      }
    }
    return new Result(builder.toString());
  }

  @Command(aliases = "create", description = "Creates a team", node = "guido.teams.create")
  public Result create(
      @Required(name = "name", description = "If the name has spaces use `_`") String name,
      @Required(name = "players", description = "The uuids of the players") @Multiple
          JoinedStrings players) {
    name = name.replace("_", " ");
    Set<TeamMember> members = new HashSet<>();
    Set<String> notJoined = new HashSet<>();
    for (String trimmed : players.getStrings()) {
      BotLinkable player =
          Guido.getDataLoader()
              .getLinkedData(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", trimmed));
      if (player != null) {
        if (members.isEmpty()) {
          members.add(new GuidoTeamMember(player.getInfo(), TeamRole.LEADER));
        } else {
          members.add(new GuidoTeamMember(player.getInfo(), TeamRole.NORMAL));
        }
      } else {
        notJoined.add(trimmed);
      }
    }
    if (!notJoined.isEmpty())
      return new Result("The next players haven't joined googas: " + Lots.pretty(notJoined));
    new GuidoTeamData(Guido.getDataLoader().nextTeamId(), name, members).cache();
    return new Result("The team " + name + " has been created");
  }
}
