package me.googas.api.events.match.team;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.team.Team;

/** An event which has a team data involved */
public class TeamDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NonNull @Getter private final Team data;

  public TeamDataEvent(@NonNull Team data) {
    this.data = data;
  }
}
