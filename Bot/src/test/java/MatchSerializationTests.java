import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.ValuesMap;
import me.googas.api.adapters.link.LinkableInfoDeserializer;
import me.googas.api.adapters.matches.MatchDeserializer;
import me.googas.api.adapters.matches.MatchTeamDeserializer;
import me.googas.api.adapters.matches.ladder.LadderDeserializer;
import me.googas.api.adapters.matches.team.TeamMemberDeserializer;
import me.googas.api.adapters.permissions.GroupDeserializer;
import me.googas.api.adapters.permissions.PermissionStackAdapter;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.adapters.links.LinkedInfoDeserializer;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkableInfo;
import me.googas.bot.core.matches.GuidoMatch;
import me.googas.bot.core.matches.GuidoMatchTeam;
import me.googas.bot.core.matches.team.GuidoTeamMember;
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
            .registerTypeAdapter(LinkableInfo.class, new LinkedInfoDeserializer(true))
            .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(MatchTeam.class, new MatchTeamDeserializer())
            .registerTypeAdapter(
                TeamMember.class, new me.googas.bot.adapters.matches.team.TeamMemberAdapter())
            .registerTypeAdapter(GuidoValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(GuidoLinkedValuesMap.class, new ValuesMapAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    Gson client =
        new GsonBuilder()
            .registerTypeAdapter(Group.class, new GroupDeserializer())
            .registerTypeAdapter(Ladder.class, new LadderDeserializer())
            .registerTypeAdapter(LinkableInfo.class, new LinkableInfoDeserializer())
            .registerTypeAdapter(Match.class, new MatchDeserializer())
            .registerTypeAdapter(
                Permission.class, new me.googas.api.adapters.permissions.PermissionAdapter())
            .registerTypeAdapter(PermissionStack.class, new PermissionStackAdapter())
            .registerTypeAdapter(MatchTeam.class, new MatchTeamDeserializer())
            .registerTypeAdapter(TeamMember.class, new TeamMemberDeserializer())
            .registerTypeAdapter(ValuesMap.class, new me.googas.api.adapters.ValuesMapAdapter())
            .registerTypeAdapter(
                SimpleValuesMap.class, new me.googas.api.adapters.ValuesMapAdapter())
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .setPrettyPrinting()
            .create();
    Set<TeamMember> members = new HashSet<>();
    GuidoLinkableInfo linkedInfo =
        new GuidoLinkableInfo(LinkableType.MINECRAFT, new GuidoValuesMap("nickname", "Selfie"));
    GuidoTeamMember guidoTeamMember = new GuidoTeamMember(linkedInfo, TeamRole.NORMAL);
    members.add(guidoTeamMember);
    String linkJson = server.toJson(linkedInfo);
    String jsonMember = server.toJson(guidoTeamMember);
    String json =
        server.toJson(
            new GuidoMatch(
                "asdasd",
                20,
                Lots.set(new GuidoMatchTeam(1, members, "asd")),
                new GuidoLinkedValuesMap("asd", "asc").put("type", "pgm").put("pito", 30)));
    Match match = client.fromJson(json, Match.class);
    System.out.println(match.getDetails());
    System.out.println(match);
  }
}
