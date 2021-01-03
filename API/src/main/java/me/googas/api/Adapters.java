package me.googas.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.api.adapters.ValuesMapAdapter;
import me.googas.api.adapters.link.LinkableInfoDeserializer;
import me.googas.api.adapters.matches.MatchDeserializer;
import me.googas.api.adapters.matches.MatchTeamDeserializer;
import me.googas.api.adapters.matches.ladder.LadderDeserializer;
import me.googas.api.adapters.matches.team.TeamDeserializer;
import me.googas.api.adapters.matches.team.TeamMemberDeserializer;
import me.googas.api.adapters.permissions.GroupDeserializer;
import me.googas.api.adapters.permissions.PermissionAdapter;
import me.googas.api.adapters.permissions.PermissionStackDeserializer;
import me.googas.api.adapters.punishment.PunishmentDeserializer;
import me.googas.api.client.data.SimpleValuesMap;
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
        .registerTypeAdapter(Group.class, new GroupDeserializer())
        .registerTypeAdapter(Ladder.class, new LadderDeserializer())
        .registerTypeAdapter(LinkableInfo.class, new LinkableInfoDeserializer())
        .registerTypeAdapter(Match.class, new MatchDeserializer())
        .registerTypeAdapter(Permission.class, new PermissionAdapter())
        .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
        .registerTypeAdapter(Punishment.class, new PunishmentDeserializer())
        .registerTypeAdapter(MatchTeam.class, new MatchTeamDeserializer())
        .registerTypeAdapter(Team.class, new TeamDeserializer())
        .registerTypeAdapter(TeamMember.class, new TeamMemberDeserializer())
        .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
        .registerTypeAdapter(SimpleValuesMap.class, new ValuesMapAdapter())
        // .setPrettyPrinting() probably not needed
        .create();
  }
}
