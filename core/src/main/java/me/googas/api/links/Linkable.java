package me.googas.api.links;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.loader.Loader;
import me.googas.api.user.UserData;

/** This object represents data or an account that can be linked to a user */
public interface Linkable {

  @NonNull
  Optional<UUID> getLinkedUserId();

  @NonNull
  String getPublicDisplayName(@NonNull Loader loader);

  void setLinkedUser(@NonNull UserData user);

  default boolean isLinked() {
    return this.getLinkedUserId().isPresent();
  }
}
