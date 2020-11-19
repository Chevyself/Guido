package me.googas.bot.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import me.googas.bot.core.Guido;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** y Static utilities for mentions */
public class Discord {

  /** A consumer which can be used to log exceptions in discord operations */
  @NotNull
  private static final Consumer<Throwable> EXCEPTION_CONSUMER =
      throwable ->
          Guido.getLogger()
              .log(
                  Level.SEVERE,
                  throwable,
                  () -> "There's been an error while doing a discord action");

  /** All the permissions required to join the voice channel */
  @NotNull
  public static final List<Permission> VOICE =
      Lots.list(
          Permission.VIEW_CHANNEL,
          Permission.VOICE_CONNECT,
          Permission.VOICE_SPEAK,
          Permission.VOICE_STREAM,
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
   * @param ignored the permissions to ignore removing and will be allowed if not
   */
  public static void removeEveryonePermissions(
      @NotNull GuildChannel channel, @NotNull Permission... ignored) {
    PermissionOverride override = channel.getPermissionOverride(channel.getGuild().getPublicRole());
    if (override != null) {
      Set<Permission> toRemove = Lots.set(Permission.values());
      Set<Permission> toAllow = new HashSet<>();
      for (Permission permission : ignored) {
        toRemove.removeIf(
            perm -> {
              if (perm.equals(permission)) {
                toAllow.add(permission);
                return true;
              }
              return false;
            });
      }
      override
          .getManager()
          .setDeny(toRemove)
          .queue(
              permissionOverride ->
                  permissionOverride
                      .getManager()
                      .setAllow(toAllow)
                      .queue(ignoredOverride -> {}, Discord.exceptionConsumer()),
              Discord.exceptionConsumer());
    }
  }

  /**
   * Remove all permissions from a channel
   *
   * @param channel the channel to remove all permissions
   * @param ignored the permissions to ignore removing
   */
  public static void removeAllPermission(
      @NotNull GuildChannel channel, @NotNull Permission... ignored) {
    for (PermissionOverride override : channel.getPermissionOverrides()) {
      if (override.getPermissionHolder() != null
          && !override.getPermissionHolder().equals(channel.getGuild().getPublicRole())) {
        override.delete().queue(aVoid -> {}, Discord.exceptionConsumer());
      }
    }
    Discord.removeEveryonePermissions(channel, ignored);
  }

  /**
   * Add permissions for a permission holder in a channel
   *
   * @param channel the channel to add the permissions
   * @param holders the permission holders to add the permissions to
   * @param permissions the permissions to add to the permission holders
   * @param success what to call when the permission is given
   */
  public static void addPermissions(
      @NotNull GuildChannel channel,
      @NotNull Collection<? extends IPermissionHolder> holders,
      @NotNull Collection<Permission> permissions,
      @Nullable Consumer<Void> success) {
    for (IPermissionHolder holder : holders) {
      Discord.addPermissions(channel, holder, permissions, success);
    }
  }

  /**
   * Add permissions for a permission holder in a channel
   *
   * @param channel the channel to add the permissions
   * @param holder the permission holder to add the permission to
   * @param permissions the permissions to add to the permission holders
   * @param success what to call when the permission is given
   */
  public static void addPermissions(
      @NotNull GuildChannel channel,
      @NotNull IPermissionHolder holder,
      @NotNull Collection<Permission> permissions,
      @Nullable Consumer<Void> success) {
    PermissionOverride override = channel.getPermissionOverride(holder);
    if (override != null) {
      override
          .getManager()
          .setAllow(permissions)
          .queue(
              permOverride -> {
                if (success != null) {
                  success.accept(null);
                }
              },
              Discord.exceptionConsumer());
    } else {
      channel
          .createPermissionOverride(holder)
          .queue(
              newOverride ->
                  newOverride
                      .getManager()
                      .setAllow(permissions)
                      .queue(
                          permOverride -> {
                            if (success != null) {
                              success.accept(null);
                            }
                          },
                          Discord.exceptionConsumer()));
    }
  }

  /**
   * Get the consumer to handle exceptions in discord operations
   *
   * @return the consumer for throwable
   */
  public static Consumer<Throwable> exceptionConsumer() {
    return Discord.EXCEPTION_CONSUMER;
  }
}
