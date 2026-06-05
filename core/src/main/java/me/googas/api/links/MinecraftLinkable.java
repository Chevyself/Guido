package me.googas.api.links;

import java.util.UUID;
import lombok.NonNull;

/** This interface represents a linkable Minecraft account */
public interface MinecraftLinkable extends Linkable {

  @NonNull
  String getNickname();

  /**
   * Get whether the player is inside the bungee server
   *
   * @return true if the user is online
   */
  boolean isOnline();

  /**
   * Get the unique id of this minecraft link
   *
   * @return the unique id
   * @throws IllegalArgumentException if the uuid is malformed
   */
  @NonNull
  UUID getId();

  @Override
  default @NonNull String getPublicDisplayName(@NonNull LinkableMatcher linkableMatcher) {
    return this.getNickname();
  }
}
