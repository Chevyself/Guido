package me.googas.bot.core.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.matches.TeamData;
import me.googas.api.matches.TeamMember;
import me.googas.bot.api.events.match.team.TeamDataUnloadedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

public class GuidoTeamData implements TeamData, BotCatchable {

  @NonNull private final String id;
  @NonNull private final Set<TeamMember> members;
  @NonNull @Setter private String name;

  public GuidoTeamData(@NonNull String id, @NonNull String name, @NonNull Set<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
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
}
