package me.googas.api.matches.queue;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;

/** A queue is joined by players to start playing */
public interface MinecraftQueue {

  @NonNull
  QueueResult join(@NonNull MinecraftLinkable minecraft);

  @NonNull
  QueueResult leave(@NonNull MinecraftLinkable minecraft);

  default boolean isWaiting(@NonNull MinecraftLinkable minecraft) {
    return this.getWaiting().contains(minecraft.getId());
  }

  /**
   * Check whether the queue is ready for a match. If the match is ready a new match will be started
   *
   * @return the match if the queue is ready
   */
  @NonNull
  Optional<MinecraftMatch> checkReady();

  /**
   * Get the linked data of the users that are waiting
   *
   * @return the linked data
   */
  @NonNull
  Collection<UUID> getWaiting();

  /**
   * Get the ladder that this queue is playing
   *
   * @return the ladder
   */
  @NonNull
  String getLadderName();

  void leave(@NonNull MinecraftMatchTeamMember member);
}
