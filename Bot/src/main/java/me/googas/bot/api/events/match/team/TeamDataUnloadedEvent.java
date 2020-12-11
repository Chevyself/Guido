package me.googas.bot.api.events.match.team;

import lombok.NonNull;
import me.googas.api.matches.TeamData;

/** Called when the team data gets unloaded */
public class TeamDataUnloadedEvent extends TeamDataEvent {

  public TeamDataUnloadedEvent(@NonNull TeamData data) {
    super(data);
  }
}
