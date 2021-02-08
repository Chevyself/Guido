package me.googas.api.events.punishment;

import lombok.NonNull;
import me.googas.api.punishment.Punishment;

/** Called when a punishment gets unloaded */
public class PunishmentUnloadedEvent extends PunishmentEvent {

  public PunishmentUnloadedEvent(@NonNull Punishment punishment) {
    super(punishment);
  }
}
