import com.google.gson.Gson;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.utility.Adapters;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.Lots;

public class TeamAdapterTest {

  public static void main(String[] args) {
    SimpleMatchTeam team =
        new SimpleMatchTeam(
            -3,
            "asd",
            Lots.set(
                new SimpleTeamMember(
                    new SimpleLinkableInfo(
                        LinkableType.MINECRAFT,
                        new SimpleValuesMap("uuid", "5eed208dde5840229ba76ccb5ea7e92a")),
                    TeamRole.LEADER)));
    Gson gson = Adapters.buildClient();
    String json = gson.toJson(team);
    System.out.println("json = " + json);
    MatchTeam teamMember = Mongo.GSON.fromJson(json, MatchTeam.class);
    System.out.println("teamMember = " + teamMember);
  }
}
