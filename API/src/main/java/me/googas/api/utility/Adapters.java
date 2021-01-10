package me.googas.api.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.api.ValuesMap;
import me.googas.api.adapters.ValuesMapAdapter;
import me.googas.api.adapters.link.LinkableDeserializer;
import me.googas.api.adapters.link.LinkableInfoDeserializer;
import me.googas.api.adapters.matches.MatchDeserializer;
import me.googas.api.adapters.matches.MatchTeamAdapter;
import me.googas.api.adapters.matches.ladder.LadderDeserializer;
import me.googas.api.adapters.matches.team.TeamDeserializer;
import me.googas.api.adapters.matches.team.TeamMemberAdapter;
import me.googas.api.adapters.permissions.GroupDeserializer;
import me.googas.api.adapters.permissions.PermissionAdapter;
import me.googas.api.adapters.permissions.PermissionStackAdapter;
import me.googas.api.adapters.punishment.PunishmentDeserializer;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.Team;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.punishment.Punishment;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;

public class Adapters {

  public static Gson buildClient() {
    return new GsonBuilder()
        // Required by messengers
        .registerTypeAdapter(Message.class, new MessageDeserializer())
        // Required for requests
        .registerTypeAdapter(Linkable.class, new LinkableDeserializer())
        .registerTypeAdapter(LinkableInfo.class, new LinkableInfoDeserializer())
        .registerTypeAdapter(Ladder.class, new LadderDeserializer())
        .registerTypeAdapter(Team.class, new TeamDeserializer())
        .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
        .registerTypeAdapter(Match.class, new MatchDeserializer())
        .registerTypeAdapter(MatchTeam.class, new MatchTeamAdapter())
        .registerTypeAdapter(Group.class, new GroupDeserializer())
        .registerTypeAdapter(Permission.class, new PermissionAdapter())
        .registerTypeAdapter(PermissionStack.class, new PermissionStackAdapter())
        .registerTypeAdapter(Punishment.class, new PunishmentDeserializer())
        .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
        .registerTypeAdapter(SimpleValuesMap.class, new ValuesMapAdapter())
        // .setPrettyPrinting() probably not needed
        .create();
  }
}
