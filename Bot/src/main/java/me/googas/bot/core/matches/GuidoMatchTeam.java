package me.googas.bot.core.matches;

import java.util.Collection;
import java.util.Objects;
import lombok.NonNull;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.commons.builder.ToStringBuilder;

/** An implementation for team */
public class GuidoMatchTeam implements MatchTeam {

  /** The id of the team */
  private final int id;

  /** The name of the team */
  @NonNull private final String name;

  /** The members of the team */
  @NonNull private final Collection<TeamMember> members;

  /**
   * Create the team
   *
   * @param id the id of the team
   * @param members the members inside the team
   * @param name the name of the team
   */
  public GuidoMatchTeam(int id, @NonNull Collection<TeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  @Override
  public @NonNull String getName() {
    return this.name;
  }

  @Override
  public boolean add(@NonNull TeamMember member) {
    return false;
  }

  @Override
  public boolean remove(@NonNull TeamMember member) {
    return false;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public @NonNull Collection<TeamMember> getMembers() {
    return this.members;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("name", this.name)
        .append("members", this.members)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoMatchTeam that = (GuidoMatchTeam) o;
    return this.id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
