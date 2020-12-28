package me.googas.bot.api.types.loader;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.loader.DataLoader;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.bot.core.handlers.GuidoHandler;

/** Loads the data for the bot */
public interface BotDataLoader extends DataLoader, GuidoHandler {

  /**
   * Get the discord data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NonNull
  Linkable getDiscordUserData(long userId);

  /**
   * Get all the member data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NonNull
  Collection<Linkable> getDiscordData(long userId);

  /**
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @NonNull
  BotRole getRoleData(long id, long guildId);

  /**
   * Load the data of a guild. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @NonNull
  BotGuild getGuildDataOrCreate(long id);

  /**
   * Load the data of a guild
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  BotGuild getGuildData(long id);
}
