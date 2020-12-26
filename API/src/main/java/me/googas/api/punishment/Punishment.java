package me.googas.api.punishment;

import lombok.NonNull;
import me.googas.api.Expirable;
import me.googas.api.GuidoCatchable;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableInfo;

/** This class represents a punishment which can be done to any kind of data */
public interface Punishment extends GuidoCatchable, Expirable {

  /**
   * Get the id of the punishment
   *
   * @return the id of punishment
   */
  @NonNull
  String getId();

  /**
   * Get the type of punishment
   *
   * @return the type of punishment
   */
  @NonNull
  PunishmentType getType();

  /**
   * Get the status of the punishment
   *
   * @return the status of the punishment
   */
  @NonNull
  PunishmentStatus getStatus();

  /**
   * Get the info of who issued the punishment
   *
   * @return the info of the punisher
   */
  LinkableInfo getPunisher();

  /**
   * Get the info of who received the punishment
   *
   * @return the info of the punished
   */
  LinkableInfo getPunished();

  /**
   * Get the details of the punishment
   *
   * @return the details of the punishment
   */
  @NonNull
  ValuesMap getDetails();
}
