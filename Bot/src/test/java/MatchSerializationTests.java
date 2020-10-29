import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starfishst.bot.adapters.LinkedInfoAdapter;
import com.starfishst.bot.adapters.PermissionAdapter;
import com.starfishst.bot.adapters.TeamMemberAdapter;
import com.starfishst.bot.adapters.ValuesMapAdapter;
import com.starfishst.bot.handlers.data.types.GuidoLinkedInfo;
import com.starfishst.bot.handlers.data.types.GuidoMatch;
import com.starfishst.bot.handlers.data.types.GuidoTeam;
import com.starfishst.bot.handlers.data.types.GuidoTeamMember;
import com.starfishst.bot.handlers.data.types.maps.GuidoLinkedValuesMap;
import com.starfishst.bot.handlers.data.types.maps.GuidoValuesMap;
import me.googas.api.Group;
import me.googas.api.Permission;
import me.googas.api.PermissionStack;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.ValuesMapImpl;
import me.googas.api.client.data.adapters.GroupDeserializer;
import me.googas.api.client.data.adapters.LadderDeserializer;
import me.googas.api.client.data.adapters.MatchDeserializer;
import me.googas.api.client.data.adapters.PermissionStackDeserializer;
import me.googas.api.client.data.adapters.TeamDeserializer;
import me.googas.api.client.data.adapters.TeamMemberDeserializer;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import java.util.HashSet;
import java.util.Set;

import me.googas.api.client.data.adapters.LinkedInfoDeserializer;
import me.googas.commons.Lots;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;

public class MatchSerializationTests {

  public static void main(String[] args) {
    Gson server =
        new GsonBuilder()
            // Required by Commons-Communication
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            // For custom receptors
            .registerTypeAdapter(LinkedInfo.class, new LinkedInfoAdapter())
            .registerTypeAdapter(Permission.class, new PermissionAdapter())
            .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(Team.class, new TeamDeserializer())
            .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
            .registerTypeAdapter(GuidoValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(GuidoLinkedValuesMap.class, new ValuesMapAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    Gson client =
        new GsonBuilder()
            .registerTypeAdapter(Group.class, new GroupDeserializer())
            .registerTypeAdapter(Ladder.class, new LadderDeserializer())
            .registerTypeAdapter(
                LinkedInfo.class,
                new LinkedInfoDeserializer())
            .registerTypeAdapter(Match.class, new MatchDeserializer())
            .registerTypeAdapter(
                Permission.class,
                new me.googas.api.client.data.adapters.PermissionAdapter())
            .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
            .registerTypeAdapter(Team.class, new TeamDeserializer())
            .registerTypeAdapter(TeamMember.class, new TeamMemberDeserializer())
            .registerTypeAdapter(
                ValuesMap.class,
                new me.googas.api.client.data.adapters.ValuesMapAdapter())
            .registerTypeAdapter(
                ValuesMapImpl.class,
                new me.googas.api.client.data.adapters.ValuesMapAdapter())
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .setPrettyPrinting()
            .create();
    Set<TeamMember> members = new HashSet<>();
    GuidoLinkedInfo linkedInfo =
        new GuidoLinkedInfo(LinkedDataType.MINECRAFT, new GuidoValuesMap("nickname", "Selfie"));
    GuidoTeamMember guidoTeamMember = new GuidoTeamMember(linkedInfo, TeamRole.NORMAL);
    members.add(guidoTeamMember);
    String linkJson = server.toJson(linkedInfo);
    String jsonMember = server.toJson(guidoTeamMember);
    String json =
        server.toJson(
            new GuidoMatch(
                "asdasd",
                20,
                Lots.set(new GuidoTeam(members, "asd")),
                new GuidoLinkedValuesMap("asd", "asc")
                    .addValue("type", "pgm")
                    .addValue("pito", 30)));
    Match match = client.fromJson(json, Match.class);
    System.out.println(match.getDetails());
    System.out.println(match);
  }
}
