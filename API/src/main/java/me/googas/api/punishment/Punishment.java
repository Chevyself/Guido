package me.googas.api.punishment;

import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This class represents a punishment which can be done to any kind of data */
public interface Punishment extends Catchable {

  /**
   * Get the type of punishment
   *
   * @return the type of punishment
   */
  @NotNull
  PunishmentType getType();

  /**
   * Get the status of the punishment
   *
   * @return the status of the punishment
   */
  @NotNull
  PunishmentStatus getStatus();

  /**
   * Get the info of who issued the punishment
   *
   * @return the info of the punisher
   */
  @Nullable
  LinkableInfo getPunisher();

  /**
   * Get the info of who received the punishment
   *
   * @return the info of the punished
   */
  @NotNull
  LinkableInfo getPunished();

  /**
   * Get the details of the punishment
   *
   * @return the details of the punishment
   */
  @NotNull
  ValuesMap getDetails();
}
