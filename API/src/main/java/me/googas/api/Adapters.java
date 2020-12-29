package me.googas.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.api.adapters.ValuesMapAdapter;
import me.googas.api.adapters.link.LinkableInfoAdapter;
import me.googas.api.adapters.matches.MatchAdapter;
import me.googas.api.adapters.matches.MatchTeamAdapter;
import me.googas.api.adapters.matches.ladder.LadderAdapter;
import me.googas.api.adapters.matches.team.TeamAdapter;
import me.googas.api.adapters.matches.team.TeamMemberAdapter;
import me.googas.api.adapters.permissions.GroupAdapter;
import me.googas.api.adapters.permissions.PermissionAdapter;
import me.googas.api.adapters.permissions.PermissionStackAdapter;
import me.googas.api.adapters.punishment.PunishmentAdapter;
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
        .registerTypeAdapter(Group.class, new GroupAdapter())
        .registerTypeAdapter(Ladder.class, new LadderAdapter())
        .registerTypeAdapter(LinkableInfo.class, new LinkableInfoAdapter())
        .registerTypeAdapter(Match.class, new MatchAdapter())
        .registerTypeAdapter(Permission.class, new PermissionAdapter())
        .registerTypeAdapter(PermissionStack.class, new PermissionStackAdapter())
        .registerTypeAdapter(Punishment.class, new PunishmentAdapter())
        .registerTypeAdapter(MatchTeam.class, new MatchTeamAdapter())
        .registerTypeAdapter(Team.class, new TeamAdapter())
        .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
        .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
        .registerTypeAdapter(SimpleValuesMap.class, new ValuesMapAdapter())
        // .setPrettyPrinting() probably not needed
        .create();
  }
}
