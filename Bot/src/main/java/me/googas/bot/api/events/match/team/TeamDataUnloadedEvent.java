package me.googas.bot.api.events.match.team;

import lombok.NonNull;
import me.googas.api.matches.team.Team;

/** Called when the team data gets unloaded */
public class TeamDataUnloadedEvent extends TeamDataEvent {

  public TeamDataUnloadedEvent(@NonNull Team data) {
    super(data);
  }
}
