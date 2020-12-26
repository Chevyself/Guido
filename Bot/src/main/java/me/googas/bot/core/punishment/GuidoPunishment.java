package me.googas.bot.core.punishment;

import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.bot.api.events.punishment.PunishmentUnloadedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class GuidoPunishment implements Punishment, BotCatchable {

  @NonNull private final String id;
  @NonNull private final PunishmentType type;
  @NonNull private final PunishmentStatus status;
  private final LinkableInfo punisher;
  private final LinkableInfo punished;
  private final GuidoValuesMap details;
  private final long expires;

  public GuidoPunishment(
      @NonNull String id,
      @NonNull PunishmentType type,
      @NonNull PunishmentStatus status,
      LinkableInfo punisher,
      LinkableInfo punished,
      GuidoValuesMap details,
      long expires) {
    this.id = id;
    this.type = type;
    this.status = status;
    this.punisher = punisher;
    this.punished = punished;
    this.details = details;
    this.expires = expires;
  }

  @Override
  public @NonNull GuidoPunishment cache() {
    return (GuidoPunishment) BotCatchable.super.cache();
  }

  @Override
  public void unload(boolean onRemove) {}

  @Override
  public long expires() {
    return this.expires;
  }

  @Override
  public void onRemove() {
    new PunishmentUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public @NonNull PunishmentType getType() {
    return this.type;
  }

  @Override
  public @NonNull PunishmentStatus getStatus() {
    return this.status;
  }

  @Override
  public LinkableInfo getPunisher() {
    return this.punisher;
  }

  @Override
  public @NonNull LinkableInfo getPunished() {
    return this.punished;
  }

  @Override
  public @NonNull ValuesMap getDetails() {
    return this.details;
  }
}
