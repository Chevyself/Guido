package me.googas.api.loader;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.commons.RandomUtils;

public interface PunishmentLoader extends DataLoader {

  /**
   * Get a punishment by its id
   *
   * @param id the id of the punishment to match
   * @return the punishment
   */
  Punishment getPunishment(@NonNull String id);

  /**
   * Get all the punishments in the given status of a link
   *
   * @param link the link to getId the punishments
   * @param statuses the statuses that the punishment may have to be included
   * @return the collection of matching punishments
   */
  @NonNull
  Collection<Punishment> getPunishments(
      @NonNull LinkableInfo link, @NonNull PunishmentStatus... statuses);

  /**
   * Get a new id for a punishment
   *
   * @return the id of the new punishment
   */
  @NonNull
  default String nextPunishmentId() {
    String id = RandomUtils.nextString(6);
    if (this.getPunishment(id) != null) return this.nextPunishmentId();
    return id;
  }
}
