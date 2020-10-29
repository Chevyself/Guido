package com.starfishst.bot.handlers.data.loader;

import com.starfishst.bot.api.data.BotGroup;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.bot.api.events.data.role.BotRoleUnloadedEvent;
import com.starfishst.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.api.Group;
import me.googas.api.UserData;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedDataType;
import me.googas.api.token.AuthToken;
import java.util.Collection;
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

  /**
   * Load the data of a guild
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @Override
  public @NotNull BotGuild getGuildDataOrCreate(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public @Nullable BotGuild getGuildData(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  /**
   * Load the data of a role
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  @Override
  public @Nullable UserData getUserData(@Nullable String id) {
    return null;
  }

  /**
   * Get linked data using it's type and identifications
   *
   * @param type the type of data to find
   * @param identifications the way to identify the data
   * @param equal
   * @return the linked data if found else null
   */
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
  public @Nullable BotGroup getGroup(@NotNull String id) {
    throw new UnsupportedOperationException("Groups have not been implemented yet");
  }

  @Override
  public @NotNull Collection<Group> getGroups() {
    throw new UnsupportedOperationException("There's no groups");
  }

  /**
   * Get the discord data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @Override
  public @NotNull BotLinkedData getDiscordUserData(long userId) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  /**
   * Get the discord data for a member
   *
   * @param userId the id of the user to get the data
   * @param guildId the id of the guild where the user is member from
   * @return the data of the user
   */
  @Override
  public @NotNull BotLinkedData getMemberData(long userId, long guildId) {
    throw new UnsupportedOperationException("Link data cannot be find using file loader");
  }

  /**
   * Get the links from an user
   *
   * @param user the user to get the links from
   * @return the links
   */
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

  /**
   * Get all the member data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @Override
  public @NotNull Collection<BotLinkedData> getDiscordData(long userId) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    throw new UnsupportedOperationException("Auth tokens cannot be find using file loader");
  }
}
