package me.googas.bot.api.events.punishment;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.punishment.Punishment;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.commons.builder.ToStringBuilder;

/** An event which has a punishment related to it */
public class PunishmentEvent implements GuidoEvent {

  @NonNull @Getter private final Punishment punishment;

  public PunishmentEvent(@NonNull Punishment punishment) {
    this.punishment = punishment;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("punishment", this.punishment).build();
  }
}
