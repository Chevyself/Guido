package me.googas.api.events.punishment;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;

public class PunishmentStatusUpdatedEvent extends PunishmentEvent {

  @Getter private final PunishmentStatus status;

  public PunishmentStatusUpdatedEvent(@NonNull Punishment punishment, PunishmentStatus status) {
    super(punishment);
    this.status = status;
  }
}
