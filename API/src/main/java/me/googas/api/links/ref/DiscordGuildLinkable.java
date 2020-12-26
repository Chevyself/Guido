package me.googas.api.links.ref;

import java.lang.ref.SoftReference;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.commons.Validate;

/** This class represents a soft reference done to a linkable from a discord guild */
public class DiscordGuildLinkable {

  @NonNull @Delegate private final SoftReference<Linkable> reference;

  /**
   * Create the linkable
   *
   * @param reference the discord guild link this implies that the link must be a type of discord
   *     guild else
   * @throws IllegalArgumentException if the link does not {@link Linkable#getType()} == {@link
   *     LinkableType#DISCORD_GUILD}
   */
  public DiscordGuildLinkable(@NonNull Linkable reference) {
    if (reference.getType() != LinkableType.DISCORD_GUILD)
      throw new IllegalArgumentException("Expected a discord link but got " + reference);
    this.reference = new SoftReference<>(reference);
  }

  @NonNull
  @Delegate
  public Linkable validated() {
    return Validate.notNull(this.reference.get(), "Reference is no longer in memory");
  }
}
