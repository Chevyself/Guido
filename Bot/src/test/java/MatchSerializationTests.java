import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.client.data.ValuesMapImpl;
import me.googas.api.client.data.adapters.GroupAdapter;
import me.googas.api.client.data.adapters.LadderAdapter;
import me.googas.api.client.data.adapters.LinkedInfoAdapter;
import me.googas.api.client.data.adapters.MatchAdapter;
import me.googas.api.client.data.adapters.PermissionStackAdapter;
import me.googas.api.client.data.adapters.TeamAdapter;
import me.googas.api.client.data.adapters.TeamMemberAdapter;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.adapters.PermissionAdapter;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.core.types.GuidoLinkedInfo;
import me.googas.bot.core.types.GuidoMatch;
import me.googas.bot.core.types.GuidoTeam;
import me.googas.bot.core.types.GuidoTeamMember;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.core.types.maps.GuidoValuesMap;
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
            .registerTypeAdapter(LinkedInfo.class, new me.googas.bot.adapters.LinkedInfoAdapter())
            .registerTypeAdapter(Permission.class, new PermissionAdapter())
            .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(Team.class, new TeamAdapter())
            .registerTypeAdapter(TeamMember.class, new me.googas.bot.adapters.TeamMemberAdapter())
            .registerTypeAdapter(GuidoValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(GuidoLinkedValuesMap.class, new ValuesMapAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    Gson client =
        new GsonBuilder()
            .registerTypeAdapter(Group.class, new GroupAdapter())
            .registerTypeAdapter(Ladder.class, new LadderAdapter())
            .registerTypeAdapter(LinkedInfo.class, new LinkedInfoAdapter())
            .registerTypeAdapter(Match.class, new MatchAdapter())
            .registerTypeAdapter(
                Permission.class, new me.googas.api.client.data.adapters.PermissionAdapter())
            .registerTypeAdapter(PermissionStack.class, new PermissionStackAdapter())
            .registerTypeAdapter(Team.class, new TeamAdapter())
            .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
            .registerTypeAdapter(
                ValuesMap.class, new me.googas.api.client.data.adapters.ValuesMapAdapter())
            .registerTypeAdapter(
                ValuesMapImpl.class, new me.googas.api.client.data.adapters.ValuesMapAdapter())
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
                Lots.set(new GuidoTeam(1, members, "asd")),
                new GuidoLinkedValuesMap("asd", "asc")
                    .addValue("type", "pgm")
                    .addValue("pito", 30)));
    Match match = client.fromJson(json, Match.class);
    System.out.println(match.getDetails());
    System.out.println(match);
  }
}
