import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkableInfo;
import me.googas.bot.core.matches.GuidoMatch;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.Lots;

public class MatchSerializationTests {

  public static void main(String[] args) {
    Set<TeamMember> members = new HashSet<>();
    GuidoLinkableInfo linkedInfo =
        new GuidoLinkableInfo(LinkableType.MINECRAFT, new GuidoValuesMap("nickname", "Selfie"));
    SimpleTeamMember guidoTeamMember = new SimpleTeamMember(linkedInfo, TeamRole.NORMAL);
    members.add(guidoTeamMember);
    Match match =
        new GuidoMatch(
            "asdasd",
            20,
            Lots.set(new SimpleMatchTeam(-2, "asd", members)),
            new GuidoLinkedValuesMap("asd", "asc").put("type", "pgm").put("pito", 30));
    MatchSerializationTests.serialize(match);
  }

  public static void serialize(@NonNull Match match) {
    String json = Mongo.constructGson(false).toJson(match);
    System.out.println(json);
  }
}
