import me.googas.api.client.data.LinkedInfoImpl;
import me.googas.api.client.data.TeamImpl;
import me.googas.api.client.data.TeamMemberImpl;
import me.googas.api.client.data.ValuesMapImpl;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.commons.Lots;
import me.googas.commons.RandomUtils;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.tracker.info.PlayerInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PicksTest {

    @Nullable
    public static TeamMember currentlyPicking;

    public static void main(String[] args) {
        LinkedInfoImpl player1 = new LinkedInfoImpl(LinkedDataType.MINECRAFT, new ValuesMapImpl(Maps.singleton("nickname", "Player 1")));
        LinkedInfoImpl player2 = new LinkedInfoImpl(LinkedDataType.MINECRAFT, new ValuesMapImpl(Maps.singleton("nickname", "Player 2")));
        LinkedInfoImpl player3 = new LinkedInfoImpl(LinkedDataType.MINECRAFT, new ValuesMapImpl(Maps.singleton("nickname", "Player 3")));
        LinkedInfoImpl player4 = new LinkedInfoImpl(LinkedDataType.MINECRAFT, new ValuesMapImpl(Maps.singleton("nickname", "Player 4")));
        LinkedInfoImpl player5 = new LinkedInfoImpl(LinkedDataType.MINECRAFT, new ValuesMapImpl(Maps.singleton("nickname", "Player 5")));
        LinkedInfoImpl player6 = new LinkedInfoImpl(LinkedDataType.MINECRAFT, new ValuesMapImpl(Maps.singleton("nickname", "Player 6")));
        Set<LinkedInfo> playersLeft = new HashSet<>(Lots.list(player1, player2, player3, player4, player5, player6));
        List<LinkedInfo> captains = RandomUtils.getRandom(new ArrayList<>(playersLeft), 2);
        List<Team> teams = new ArrayList<>();
        playersLeft.removeAll(captains);
        for (LinkedInfo captain : captains) {
            String name = captain.getIdentification().getValueOr("nickname", String.class, "");
            name = name.isEmpty() ? "Team " + (captains.indexOf(captain) + 1) : name + "'s team";
            teams.add(new TeamImpl(captains.indexOf(captain), name, Lots.set(new TeamMemberImpl(captain, TeamRole.LEADER))));
        }
        PicksTest.currentlyPicking = PicksTest.getLeader(RandomUtils.getRandom(teams));
        while (!playersLeft.isEmpty()) {
            Team team = PicksTest.getTeam(teams, PicksTest.currentlyPicking);
            PicksTest.pick(playersLeft, teams, team, RandomUtils.getRandom(playersLeft));
        }
    }

    public static void pick(Set<LinkedInfo> playersLeft, List<Team> teams, Team team, @NotNull LinkedInfo selected) {
        team.getMembers().add(new TeamMemberImpl(selected, TeamRole.NORMAL));
        playersLeft.remove(selected);
        PicksTest.nextPick(playersLeft, teams, team);
    }

    public static void nextPick(@NotNull Set<LinkedInfo> playersLeft, @NotNull List<Team> teams, @NotNull Team team) {
        if (playersLeft.isEmpty()) {
            System.out.println(teams);
            System.out.println("Finished picking");
            System.exit(0);
        } else if (playersLeft.size() == 2) {
            PicksTest.currentlyPicking = PicksTest.getLeader(team);
        } else if (playersLeft.size() == 1) {
            PicksTest.currentlyPicking = PicksTest.getLeader(PicksTest.getNextTeam(teams, team));
            PicksTest.pick(playersLeft, teams, PicksTest.getNextTeam(teams, team), new ArrayList<>(playersLeft).get(0));
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
