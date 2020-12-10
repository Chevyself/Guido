package me.googas.api.punishment;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;

/** This class represents a punishment which can be done to any kind of data */
public interface Punishment extends Catchable {

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
  @NonNull
  LinkableInfo getPunished();

  /**
   * Get the details of the punishment
   *
   * @return the details of the punishment
   */
  @NonNull
  ValuesMap getDetails();
}
