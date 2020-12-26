package me.googas.api.client.data.matches.team;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.GuidoCatchable;
import me.googas.api.matches.team.Team;
import me.googas.api.matches.team.TeamMember;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class SimpleTeam implements Team {

  @NonNull private final String id;
  @NonNull private final String name;
  @NonNull private final Collection<TeamMember> members;

  public SimpleTeam(
      @NonNull String id, @NonNull String name, @NonNull Collection<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
  }

  /** @deprecated This constructor must only be used by gson */
  public SimpleTeam() {
    this("", "", new HashSet<>());
  }

  @Override
  public @NonNull GuidoCatchable cache() {
    return this;
  }

  @Override
  public void unload(boolean onRemove) {}

  @Override
  public void onRemove() {}

  @Override
  public @NonNull Time getToRemove() {
    return new Time(0, Unit.SECONDS);
  }

  @Override
  public @NonNull Map<String, Float> getStats() {
    return new HashMap<>();
  }

  @Override
  public @NonNull String getSingle() {
    return this.getName();
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public @NonNull String getName() {
    return this.name;
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
}
