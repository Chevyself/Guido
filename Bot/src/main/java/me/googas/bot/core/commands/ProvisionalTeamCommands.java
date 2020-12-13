package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
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
    Set<String> hasTeam = new HashSet<>();
    for (String trimmed : players.getStrings()) {
      BotLinkable player =
          Guido.getDataLoader()
              .getLinkedData(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", trimmed));
      if (player != null) {
        if (members.isEmpty()) {
          members.add(new GuidoTeamMember(player.getInfo(), TeamRole.LEADER));
        } else {
          if (player.getTeam() != null) {
            hasTeam.add(player.getSingle());
            continue;
          }
          members.add(new GuidoTeamMember(player.getInfo(), TeamRole.NORMAL));
        }
      } else {
        notJoined.add(trimmed);
      }
    }
    if (!notJoined.isEmpty())
      return new Result("The next players haven't joined googas: " + Lots.pretty(notJoined));
    if (!hasTeam.isEmpty())
      return new Result("The next players already have a team " + Lots.pretty(hasTeam));
    new GuidoTeamData(Guido.getDataLoader().nextTeamId(), name, members).cache();
    return new Result("The team " + name + " has been created");
  }

  @Command(aliases = "add", description = "Adds a player to the team", node = "guido.teams.add")
  public Result add(
      @Required(name = "team", description = "The team to add the player") TeamData team,
      @Required(name = "uuid", description = "The trimmed uuid of the player") String trimmed) {
    BotLinkable player =
        Guido.getDataLoader()
            .getLinkedData(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", trimmed));
    if (player == null) return new Result(ResultType.ERROR, trimmed + " hasn't joined googas yet");
    if (player.getTeam() != null)
      return new Result(ResultType.ERROR, player.getSingle() + " is already on a team");
    team.add(new GuidoTeamMember(player.getInfo(), TeamRole.NORMAL));
    return new Result(player.getSingle() + " has joined " + team.getName());
  }

  @Command(
      aliases = "remove",
      description = "Removes a player from a team",
      node = "guido.teams.remove")
  public Result remove(
      @Required(name = "team", description = "The team to remove the player") TeamData team,
      @Required(name = "uuid", description = "The trimmed uuid of the player") String trimmed) {
    BotLinkable player =
        Guido.getDataLoader()
            .getLinkedData(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", trimmed));
    if (player == null) return new Result(ResultType.ERROR, trimmed + " hasn't joined googas yet");
    if (player.getTeam() == null)
      return new Result(ResultType.ERROR, player.getSingle() + " is not on a team");
    if (!team.contains(player))
      return new Result(ResultType.ERROR, player.getSingle() + " is not on " + team.getName());
    team.remove(new GuidoTeamMember(player.getInfo(), TeamRole.NORMAL));
    return new Result(player.getSingle() + " has left " + team.getName());
  }

  @Command(aliases = "delete", description = "Deletes a team")
  public Result delete(@Required(name = "team", description = "The team to delete") TeamData team) {
    if (Guido.getDataLoader().deleteTeam(team.getId())) {
      return new Result(team.getName() + " has been deleted");
    }
    return new Result(team.getName() + " could not be deleted");
  }
}
