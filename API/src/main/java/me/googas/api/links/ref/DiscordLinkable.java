package me.googas.api.links.ref;

import java.lang.ref.SoftReference;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/** This class represents a soft reference done to a linkable from discord */
public class DiscordLinkable {

  @NonNull @Delegate private final SoftReference<Linkable> reference;

  /**
   * Create the linkable
   *
   * @param reference the discord link this implies that the link must be a type of discord else
   * @throws IllegalArgumentException if the link does not {@link Linkable#getType()} == {@link
   *     LinkableType#DISCORD}
   */
  public DiscordLinkable(@NonNull Linkable reference) {
    if (reference.getType() != LinkableType.DISCORD)
      throw new IllegalArgumentException("Expected a discord link but got " + reference);
    this.reference = new SoftReference<>(reference);
  }

  public User getUser(@NonNull JDA jda) {
    return jda.getUserById(this.getId());
  }

  public Member getMember(@NonNull Guild guild) {
    return guild.getMemberById(this.getId());
  }

  public Member getMember(long guild, @NonNull JDA jda) {
    Guild guildById = jda.getGuildById(guild);
    if (guildById != null) return this.getMember(guildById);
    return null;
  }

  public long getId() {
    return this.getIdentification().getOr("id", Long.class, 0L);
  }

  @NonNull
  @Delegate
  public Linkable validated() {
    return Validate.notNull(this.reference.get(), "Reference is no longer in memory");
  }
}
