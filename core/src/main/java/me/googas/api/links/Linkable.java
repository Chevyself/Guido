package me.googas.api.links;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

/** This object represents data or an account that can be linked to a user */
public interface Linkable {

  @NonNull
  Optional<UUID> getLinkedUserId();
}
