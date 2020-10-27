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
import com.starfishst.guido.api.data.Group;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.implementations.data.ValuesMapImpl;
import com.starfishst.guido.api.data.implementations.data.adapters.GroupDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.LadderDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.MatchDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.PermissionStackDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.TeamDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.TeamMemberDeserializer;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamMember;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.HashSet;
import java.util.Set;
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
                new com.starfishst.guido.api.data.implementations.data.adapters
                    .LinkedInfoDeserializer())
            .registerTypeAdapter(Match.class, new MatchDeserializer())
            .registerTypeAdapter(
                Permission.class,
                new com.starfishst.guido.api.data.implementations.data.adapters.PermissionAdapter())
            .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
            .registerTypeAdapter(Team.class, new TeamDeserializer())
            .registerTypeAdapter(TeamMember.class, new TeamMemberDeserializer())
            .registerTypeAdapter(
                ValuesMap.class,
                new com.starfishst.guido.api.data.implementations.data.adapters.ValuesMapAdapter())
            .registerTypeAdapter(
                ValuesMapImpl.class,
                new com.starfishst.guido.api.data.implementations.data.adapters.ValuesMapAdapter())
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
