package me.googas.api.events.punishment;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.punishment.Punishment;

public class PunishmentExpiresUpdatedEvent extends PunishmentEvent {

  @Getter private final long expires;

  public PunishmentExpiresUpdatedEvent(@NonNull Punishment punishment, long expires) {
    super(punishment);
    this.expires = expires;
  }
}
