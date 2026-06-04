package me.googas.api.loader;

import java.util.Optional;
import me.googas.api.user.UserData;

public interface UserLoader extends DataLoader {

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  Optional<UserData> getUserData(String id);
}
