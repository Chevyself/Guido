package me.googas.api.client.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.matches.TeamData;
import me.googas.api.matches.TeamMember;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class SimpleTeamData implements TeamData {

  @NonNull private final String id;

  @NonNull private final String name;

  @NonNull private final Collection<TeamMember> members;

  public SimpleTeamData(
      @NonNull String id, @NonNull String name, @NonNull Collection<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
  }

  /** @deprecated This constructor must only be used by gson */
  public SimpleTeamData() {
    this("", "", new HashSet<>());
  }

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
