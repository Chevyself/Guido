package me.googas.bot.api.events.match.team;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.TeamData;
import me.googas.bot.api.events.GuidoEvent;

/** An event which has a team data involved */
public class TeamDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NonNull @Getter private final TeamData data;

  public TeamDataEvent(@NonNull TeamData data) {
    this.data = data;
  }
}
