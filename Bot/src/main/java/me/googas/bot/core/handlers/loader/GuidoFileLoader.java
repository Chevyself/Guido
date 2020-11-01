package me.googas.bot.core.handlers.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedDataType;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.permissions.Group;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkedData;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.api.types.BotRole;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This loader will attempt to get the data from files if it fails it will create a new instance of
 * the data required
 */
public class GuidoFileLoader implements BotDataLoader {
  /**
   * This will listen to when the guild data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NotNull BotGuildUnloadedEvent event) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  /**
   * This will listen to when the role data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleDataUnloaded(@NotNull BotRoleUnloadedEvent event) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  /**
   * This will listen to when the user data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onUserDataUnloaded(@NotNull UserUnloadedDataEvent event) {
    throw new UnsupportedOperationException("User data cannot be find using file loader");
  }

  @Override
  public void close() {}

  @Override
  public @NotNull BotGuild getGuildDataOrCreate(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public @Nullable BotGuild getGuildData(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  @Override
  public @Nullable UserData getUserData(@Nullable String id) {
    return null;
  }

  @Override
  public @Nullable BotLinkedData getLinkedData(
      @NotNull LinkedDataType type, @NotNull ValuesMap identifications, boolean equal) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public @Nullable BotMatch getMatch(@NotNull String id) {
    return null;
  }

  @Override
  public long maxPageLeaderboard(@NotNull Ladder ladder, int size) {
    return 0;
  }

  @Override
  public @NotNull Collection<Match> getParticipating(
      @NotNull LinkedDataType type,
      @NotNull ValuesMap identification,
      @NotNull MatchStatus... status) {
    return new ArrayList<>();
  }

  @Override
  public @Nullable BotGroup getGroup(@NotNull String id) {
    throw new UnsupportedOperationException("Groups have not been implemented yet");
  }

  @Override
  public @NotNull Collection<Group> getGroups() {
    throw new UnsupportedOperationException("There's no groups");
  }

  @Override
  public @NotNull List<LinkedData> getLeaderboard(@NotNull Ladder ladder, int page, int size) {
    return new ArrayList<>();
  }

  @Override
  public @NotNull BotLinkedData getDiscordUserData(long userId) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public @NotNull BotLinkedData getMemberData(long userId, long guildId) {
    throw new UnsupportedOperationException("Link data cannot be find using file loader");
  }

  @Override
  public @NotNull Collection<LinkedData> getLinks(@NotNull UserData user) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public @NotNull Collection<LinkedData> getLinks(
      @NotNull UserData user, @NotNull LinkedDataType... types) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @NotNull
  @Override
  public Collection<AuthToken> getTokens(@NotNull UserData user) {
    throw new UnsupportedOperationException("File loader cannot get tokens");
  }

  @Override
  public @NotNull Collection<BotLinkedData> getDiscordData(long userId) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    throw new UnsupportedOperationException("Auth tokens cannot be find using file loader");
  }
}
