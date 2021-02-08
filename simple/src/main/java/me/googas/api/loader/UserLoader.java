package me.googas.api.loader;

import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.commons.RandomUtils;

public interface UserLoader extends DataLoader {

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  UserData getUserData(String id);

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NonNull
  default String nextUserId() {
    String id = RandomUtils.nextString(6);
    if (this.getUserData(id) != null) return this.nextUserId();
    return id;
  }
}
