package me.googas.bot.core.matches.team;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.matches.team.Team;
import me.googas.api.matches.team.TeamMember;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.match.team.TeamDataUnloadedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

public class GuidoTeam implements Team, BotCatchable {

  @NonNull private final String id;
  @NonNull private final Set<TeamMember> members;
  @NonNull @Setter private String name;

  public GuidoTeam(@NonNull String id, @NonNull String name, @NonNull Set<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
  }

  public GuidoTeam(@NonNull String name, @NonNull Set<TeamMember> members) {
    this(Guido.getHandlers().getLoader().getTeams().nextTeamId(), name, members);
  }

  @Override
  public void onRemove() {
    new TeamDataUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
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
        .append("members", this.members)
        .append("name", this.name)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoTeam guidoTeam = (GuidoTeam) o;
    return Objects.equals(this.id, guidoTeam.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
