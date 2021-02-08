package me.googas.api.punishment;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.API;
import me.googas.api.Expirable;
import me.googas.api.GuidoCatchable;
import me.googas.api.Informative;
import me.googas.api.events.punishment.PunishmentExpiresUpdatedEvent;
import me.googas.api.events.punishment.PunishmentStatusUpdatedEvent;
import me.googas.api.events.punishment.PunishmentUnloadedEvent;
import me.googas.api.links.LinkableInfo;

/** This class represents a punishment which can be done to any kind of data */
public class Punishment implements GuidoCatchable, Expirable, Informative {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final PunishmentType type;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;
  @Nullable @Getter private final LinkableInfo punisher;
  @Nullable @Getter private final LinkableInfo punished;
  @NonNull @Getter private PunishmentStatus status;
  @Getter private long expires;

  /**
   * Create the punishment
   *
   * @param id the id of the punishment
   * @param type the type of punishment
   * @param information the information of the punishment
   * @param punisher the link that issued the punishment
   * @param punished the link that got punished
   * @param status the status of the punishment
   * @param expires the date to when the punishment expires in millis
   */
  public Punishment(
      @NonNull String id,
      @NonNull PunishmentType type,
      @NonNull Map<String, Map<String, Object>> information,
      @Nullable LinkableInfo punisher,
      @Nullable LinkableInfo punished,
      @NonNull PunishmentStatus status,
      long expires) {
    this.id = id;
    this.type = type;
    this.information = information;
    this.punisher = punisher;
    this.punished = punished;
    this.status = status;
    this.expires = expires;
  }

  /** @deprecated this constructor may only be used by gson */
  public Punishment() {
    this("", PunishmentType.UNKNOWN, new HashMap<>(), null, null, PunishmentStatus.UNKNOWN, 0);
  }

  public Punishment(
      @NonNull PunishmentType type,
      @NonNull Map<String, Map<String, Object>> information,
      @Nullable LinkableInfo punisher,
      @Nullable LinkableInfo punished,
      @NonNull PunishmentStatus status,
      long expires) {
    this(
        API.getLoader().getPunishments().nextPunishmentId(),
        type,
        information,
        punisher,
        punished,
        status,
        expires);
  }

  @NonNull
  public Punishment setStatus(PunishmentStatus status) {
    this.status = status;
    new PunishmentStatusUpdatedEvent(this, status);
    return this;
  }

  @Override
  public boolean setExpires(long expires) {
    this.expires = expires;
    new PunishmentExpiresUpdatedEvent(this, expires).call();
    return true;
  }

  @Override
  public void onRemove() {
    new PunishmentUnloadedEvent(this).call();
  }
}
