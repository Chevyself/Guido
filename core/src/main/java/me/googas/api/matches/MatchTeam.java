package me.googas.api.matches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.links.Linkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;

/** This object represents a team. Which is basically a collection of members */
public class MatchTeam {

  @Getter private final int id;
  @NonNull @Getter private final Set<TeamMember> members;
  @NonNull @Getter @Setter private String name;

  /**
   * Create the match team
   *
   * @param id the id of the team
   * @param members the members of the team
   * @param name the name of the team
   */
  public MatchTeam(int id, @NonNull Set<TeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  /** @deprecated this constructor may only be used by gson */
  public MatchTeam() {
    this(0, new HashSet<>(), "");
  }

  /**
   * Calculate the average elo for a team in a ladder
   *
   * @param ladder the ladder to calculate the elo to
   * @return the average elo of the team
   */
  public float getElo(@NonNull Ladder ladder) {
    if (!this.getMembers().isEmpty()) {
      float sum = 0;
      for (TeamMember member : this.getMembers()) {
        Linkable data = member.getLink().getLink();
        if (data != null) {
          sum += data.getElo("none", ladder);
        }
      }
      return sum / this.getMembers().size();
    } else {
      return 0;
    }
  }

  /**
   * Adds a member to the team
   *
   * @param member the member to add
   * @return whether the member was added to the team
   */
  public boolean add(@NonNull TeamMember member) {
    return this.getMembers().add(member);
  }

  /**
   * Removes a member from the team
   *
   * @param member the member to remove
   * @return whether the member was removed from the team
   */
  public boolean remove(@NonNull TeamMember member) {
    return this.getMembers().remove(member);
  }

  /**
   * Get the single identification for all the members
   *
   * @return the single identification
   */
  @NonNull
  public Collection<String> getMemberSingles() {
    List<String> singles = new ArrayList<>();
    for (TeamMember member : this.getMembers()) {
      Linkable data = member.getLink().getLink();
      if (data != null) singles.add(data.getSingle());
    }
    return singles;
  }
}
