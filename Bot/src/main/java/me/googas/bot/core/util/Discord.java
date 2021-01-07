package me.googas.bot.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import lombok.CustomLog;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.bot.core.user.GuidoUser;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;

/** y Static utilities for mentions */
@CustomLog
public class Discord {

  /** All the permissions required to join the voice channel */
  @NonNull
  public static final List<Permission> VOICE =
      Lots.list(
          Permission.VIEW_CHANNEL,
          Permission.VOICE_CONNECT,
          Permission.VOICE_SPEAK,
          Permission.VOICE_STREAM,
          Permission.VOICE_USE_VAD);
  /** A consumer which can be used to log exceptions in discord operations */
  @NonNull
  private static final Consumer<Throwable> EXCEPTION_CONSUMER =
      throwable ->
          Discord.log.log(
              Level.SEVERE, throwable, () -> "There's been an error while doing a discord action");

  /**
   * Get the mentions from a collection of mentionables.
   *
   * @param mentionables the mentionables to get the mentions from
   * @param <T> the type of mentionables
   * @return the mentions of the mentionables
   */
  @NonNull
  public static <T extends IMentionable> List<String> getMentions(
      @NonNull Collection<T> mentionables) {
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
  @NonNull
  public static <T extends IMentionable> String pretty(@NonNull Collection<T> mentionables) {
    return Lots.pretty(Discord.getMentions(mentionables));
  }

  /**
   * Remove all the permissions for everyone in certain guild channel
   *
   * @param channel the channel to disallow everyone
   * @param ignored the permissions to ignore removing and will be allowed if not
   */
  public static void removeEveryonePermissions(
      @NonNull GuildChannel channel, @NonNull Permission... ignored) {
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
      @NonNull GuildChannel channel, @NonNull Permission... ignored) {
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
      @NonNull GuildChannel channel,
      @NonNull Collection<? extends IPermissionHolder> holders,
      @NonNull Collection<Permission> permissions,
      Consumer<Void> success) {
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
      @NonNull GuildChannel channel,
      @NonNull IPermissionHolder holder,
      @NonNull Collection<Permission> permissions,
      Consumer<Void> success) {
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

  @NonNull
  public static DiscordLinkable getUser(long id) {
    Linkable linkable =
        Validate.notNullOrGet(
            Guido.getHandlers()
                .getLoader()
                .getLinks()
                .getLink(LinkableType.DISCORD, new GuidoValuesMap("id", id)),
            () ->
                new GuidoLinkable(
                    LinkableType.DISCORD,
                    new GuidoValuesMap(),
                    new GuidoUser(new GuidoValuesMap()).getId(),
                    new GuidoValuesMap("id", id),
                    new GuidoValuesMap(),
                    new HashMap<>(),
                    new HashSet<>()));
    if (linkable != null) return linkable.requireDiscordRef();
    throw new IllegalStateException("Could not get linkable for user " + id);
  }

  @NonNull
  public static DiscordLinkable getUser(@NonNull User user) {
    return Discord.getUser(user.getIdLong());
  }

  @NonNull
  public static DiscordLinkable getUser(Member member) {
    return Discord.getUser(member.getIdLong());
  }
}
