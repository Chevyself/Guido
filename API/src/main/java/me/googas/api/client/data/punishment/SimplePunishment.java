package me.googas.api.client.data.punishment;

import lombok.NonNull;
import me.googas.api.GuidoCatchable;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class SimplePunishment implements Punishment {

  @NonNull private final String id;
  @NonNull private final PunishmentType type;
  @NonNull private PunishmentStatus status;
  private final LinkableInfo punisher;
  private final LinkableInfo punished;
  @NonNull private final ValuesMap details;
  private long expires;

  /**
   * Create the simple punishment
   *
   * @param id the id of the punishment
   * @param type the type of the punishment
   * @param status the status of the punishment
   * @param punisher the entity that made the punishment
   * @param punished the entity that got punished
   * @param details the details of the punishment
   * @param expires the millis to when the punishment expires
   */
  public SimplePunishment(
      @NonNull String id,
      @NonNull PunishmentType type,
      @NonNull PunishmentStatus status,
      LinkableInfo punisher,
      LinkableInfo punished,
      @NonNull ValuesMap details,
      long expires) {
    this.id = id;
    this.type = type;
    this.status = status;
    this.punisher = punisher;
    this.punished = punished;
    this.details = details;
    this.expires = expires;
  }

  /** @deprecated this constructor may only be used by gson */
  public SimplePunishment() {
    this(
        "", PunishmentType.UNKNOWN, PunishmentStatus.UNKNOWN, null, null, new SimpleValuesMap(), 0);
  }

  @Override
  public @NonNull GuidoCatchable cache() {
    return this;
  }

  @Override
  public void onRemove() {}

  @Override
  public @NonNull Time getToRemove() {
    return new Time(0, Unit.SECONDS);
  }

  @Override
  public long expires() {
    return this.expires;
  }

  @Override
  public boolean setExpires(long expires) {
    this.expires = expires;
    return true;
  }

  @Override
  public boolean setStatus(@NonNull PunishmentStatus status) {
    this.status = status;
    return true;
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
  public LinkableInfo getPunished() {
    return this.punished;
  }

  @Override
  public @NonNull ValuesMap getDetails() {
    return this.details;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", this.type)
        .append("status", this.status)
        .append("punisher", this.punisher)
        .append("punished", this.punished)
        .append("details", this.details)
        .append("expires", this.expires)
        .build();
  }
}
