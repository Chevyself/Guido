package me.googas.api.links.ref;

import java.lang.ref.SoftReference;
import java.util.UUID;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.Validate;

/** This class represents a soft reference done to a linkable from minecraft */
public class MinecraftLinkable {

  @NonNull @Delegate private final SoftReference<Linkable> reference;

  /**
   * Create the linkable
   *
   * @param reference the minecraft link this implies that the link must be a type of minecraft else
   * @throws IllegalArgumentException if the link does not {@link Linkable#getType()} == {@link
   *     LinkableType#MINECRAFT}
   */
  public MinecraftLinkable(@NonNull Linkable reference) {
    if (reference.getType() != LinkableType.MINECRAFT)
      throw new IllegalArgumentException("Expected a minecraft link but got " + reference);
    this.reference = new SoftReference<>(reference);
  }

  @NonNull
  @Delegate
  public Linkable validated() {
    return Validate.notNull(this.reference.get(), "Reference is no longer in memory");
  }

  @NonNull
  public String getNickname() {
    return this.getRecogString("nickname", "");
  }

  /**
   * Get the trimmed uuid of the {@link LinkableType#MINECRAFT}
   *
   * @return the trimmed uuid
   * @throws NullPointerException if the trimmed uuid is not found in the link
   */
  @NonNull
  public String getTrimmedUniqueId() {
    return this.getIdString("uuid", "");
  }

  /**
   * Get whether the player is inside the bungee server
   *
   * @return true if the user is online
   */
  public boolean isOnline() {
    return this.getBoolean(null, "online", false);
  }

  /**
   * Get the unique id of this minecraft link
   *
   * @return the unique id
   * @throws IllegalArgumentException if the uuid is malformed
   */
  @NonNull
  public UUID getUuid() {
    return UUIDUtils.untrim(this.getTrimmedUniqueId());
  }
}
