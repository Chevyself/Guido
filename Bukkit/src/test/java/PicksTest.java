import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.googas.api.client.data.SimpleLinkableInfo;
import me.googas.api.client.data.SimpleTeam;
import me.googas.api.client.data.SimpleTeamMember;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.commons.Lots;
import me.googas.commons.RandomUtils;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PicksTest {

  @Nullable public static TeamMember currentlyPicking;

  public static void main(String[] args) {
    SimpleLinkableInfo player1 =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT, new SimpleValuesMap(Maps.singleton("nickname", "Player 1")));
    SimpleLinkableInfo player2 =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT, new SimpleValuesMap(Maps.singleton("nickname", "Player 2")));
    SimpleLinkableInfo player3 =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT, new SimpleValuesMap(Maps.singleton("nickname", "Player 3")));
    SimpleLinkableInfo player4 =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT, new SimpleValuesMap(Maps.singleton("nickname", "Player 4")));
    SimpleLinkableInfo player5 =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT, new SimpleValuesMap(Maps.singleton("nickname", "Player 5")));
    SimpleLinkableInfo player6 =
        new SimpleLinkableInfo(
            LinkableType.MINECRAFT, new SimpleValuesMap(Maps.singleton("nickname", "Player 6")));
    Set<LinkableInfo> playersLeft =
        new HashSet<>(Lots.list(player1, player2, player3, player4, player5, player6));
    List<LinkableInfo> captains = RandomUtils.getRandom(new ArrayList<>(playersLeft), 2);
    List<Team> teams = new ArrayList<>();
    playersLeft.removeAll(captains);
    for (LinkableInfo captain : captains) {
      String name = captain.getIdentification().getOr("nickname", String.class, "");
      name = name.isEmpty() ? "Team " + (captains.indexOf(captain) + 1) : name + "'s team";
      teams.add(
          new SimpleTeam(
              captains.indexOf(captain),
              name,
              Lots.set(new SimpleTeamMember(captain, TeamRole.LEADER))));
    }
    PicksTest.currentlyPicking = PicksTest.getLeader(RandomUtils.getRandom(teams));
    while (!playersLeft.isEmpty()) {
      Team team = PicksTest.getTeam(teams, PicksTest.currentlyPicking);
      PicksTest.pick(playersLeft, teams, team, RandomUtils.getRandom(playersLeft));
    }
  }

  public static void pick(
      Set<LinkableInfo> playersLeft, List<Team> teams, Team team, @NotNull LinkableInfo selected) {
    team.getMembers().add(new SimpleTeamMember(selected, TeamRole.NORMAL));
    playersLeft.remove(selected);
    PicksTest.nextPick(playersLeft, teams, team);
  }

  public static void nextPick(
      @NotNull Set<LinkableInfo> playersLeft, @NotNull List<Team> teams, @NotNull Team team) {
    if (playersLeft.isEmpty()) {
      System.out.println(teams);
      System.out.println("Finished picking");
      System.exit(0);
    } else if (playersLeft.size() == 2) {
      PicksTest.currentlyPicking = PicksTest.getLeader(team);
    } else if (playersLeft.size() == 1) {
      PicksTest.currentlyPicking = PicksTest.getLeader(PicksTest.getNextTeam(teams, team));
      PicksTest.pick(
          playersLeft,
          teams,
          PicksTest.getNextTeam(teams, team),
          new ArrayList<>(playersLeft).get(0));
    } else {
      PicksTest.currentlyPicking = PicksTest.getLeader(PicksTest.getNextTeam(teams, team));
    }
  }

  @NotNull
  private static Team getNextTeam(List<Team> teams, Team team) {
    int index = teams.indexOf(team);
    if (index == teams.size() - 1) {
      return teams.get(0);
    }
    return teams.get(index + 1);
  }

  @NotNull
  public static TeamMember getLeader(@NotNull Team team) {
    for (TeamMember member : team.getMembers()) {
      if (member.getTeamRole() == TeamRole.LEADER) {
        return member;
      }
    }
    throw new IllegalArgumentException(team + " does not have a leader");
  }

  @NotNull
  public static Team getTeam(@NotNull Collection<Team> teams, @NotNull TeamMember member) {
    for (Team team : teams) {
      if (team.getMembers().contains(member)) {
        return team;
      }
    }
    throw new IllegalArgumentException(member + " is not inside a team");
  }
}
