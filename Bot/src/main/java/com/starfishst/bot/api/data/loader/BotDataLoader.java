package com.starfishst.bot.api.data.loader;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.loader.DataLoader;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads the data for the bot */
public interface BotDataLoader extends DataLoader, GuidoEventHandler {

  /**
   * Get the discord data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NotNull
  BotLinkedData getDiscordUserData(long userId);

  /**
   * Get the discord data for a member
   *
   * @param userId the id of the user to get the data
   * @param guildId the id of the guild where the user is member from
   * @return the data of the user
   */
  @NotNull
  BotLinkedData getMemberData(long userId, long guildId);

  /**
   * Get all the member data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NotNull
  Collection<BotLinkedData> getDiscordData(long userId);

  /**
   * Load the data of a guild. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @Override
  @NotNull
  BotGuild getGuildData(long id);

  /**
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @Override
  @NotNull
  BotRole getRoleData(long id, long guildId);

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  @Override
  @Nullable
  BotUser getUserData(@NotNull String id);

  /**
   * Get linked data using it's type and identifications
   *
   * @param type the type of data to find
   * @param identifications the way to identify the data
   * @return the linked data if found else null
   */
  @Nullable
  BotLinkedData getLinkedData(@NotNull LinkedDataType type, @NotNull ValuesMap identifications);

  /**
   * Get the links from an user
   *
   * @param user the user to get the links from
   * @return the links
   */
  @Override
  @NotNull
  Collection<BotLinkedData> getLinks(@NotNull UserData user);
}
