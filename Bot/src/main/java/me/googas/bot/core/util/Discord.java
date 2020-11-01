package me.googas.bot.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import org.jetbrains.annotations.NotNull;

/** y Static utilities for mentions */
public class Discord {

  /** All the permissions required to join the voice channel */
  @NotNull
  public static final List<Permission> VOICE =
      Lots.list(
          Permission.VOICE_CONNECT,
          Permission.VOICE_SPEAK,
          Permission.VOICE_SPEAK,
          Permission.VOICE_USE_VAD);

  /**
   * Get the mentions from a collection of mentionables.
   *
   * @param mentionables the mentionables to get the mentions from
   * @param <T> the type of mentionables
   * @return the mentions of the mentionables
   */
  @NotNull
  public static <T extends IMentionable> List<String> getMentions(
      @NotNull Collection<T> mentionables) {
    List<String> mentions = new ArrayList<>();
    for (T mentionable : mentionables) {
      mentions.add(mentionable.getAsMention());
    }
    return mentions;
  }

  /**
   * Get the mentions from a collection of mentionables in a pretty way. This will get the
   * collection of mentions and turn them into a pretty string using {@link Lots#pretty(Collection)}
   *
   * @param mentionables the mentionables to get the mentions from
   * @param <T> the type of mentionables
   * @return the mentions of the mentionables
   */
  @NotNull
  public static <T extends IMentionable> String pretty(@NotNull Collection<T> mentionables) {
    return Lots.pretty(Discord.getMentions(mentionables));
  }

  /**
   * Remove all the permissions for everyone in certain guild channel
   *
   * @param channel the channel to disallow everyone
   */
  public static void removeEveryonePermissions(@NotNull GuildChannel channel) {
    PermissionOverride override = channel.getPermissionOverride(channel.getGuild().getPublicRole());
    if (override != null) {
      override.getManager().setDeny(Permission.values()).queue();
    }
  }

  /**
   * Add permissions for a permission holder in a channel
   *
   * @param channel the channel to add the permissions
   * @param holders the permission holders to add the permissions to
   * @param permissions the permissions to add to the permission holders
   */
  public static void addPermissions(
      @NotNull GuildChannel channel,
      @NotNull Collection<? extends IPermissionHolder> holders,
      @NotNull Collection<Permission> permissions) {
    for (IPermissionHolder holder : holders) {
      Discord.addPermissions(channel, holder, permissions);
    }
  }

  /**
   * Add permissions for a permission holder in a channel
   *
   * @param channel the channel to add the permissions
   * @param holder the permission holder to add the permission to
   * @param permissions the permissions to add to the permission holders
   */
  public static void addPermissions(
      @NotNull GuildChannel channel,
      @NotNull IPermissionHolder holder,
      @NotNull Collection<Permission> permissions) {
    PermissionOverride override = channel.getPermissionOverride(holder);
    if (override != null) {
      override.getManager().setAllow(permissions).queue();
    } else {
      channel
          .createPermissionOverride(holder)
          .queue(
              newOverride -> {
                newOverride.getManager().setAllow(permissions).queue();
              });
    }
  }
}
