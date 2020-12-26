package me.googas.bot.api.types.loader;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.DataLoader;
import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.bot.api.types.links.BotLinkable;
import me.googas.bot.core.handlers.GuidoEventHandler;

/** Loads the data for the bot */
public interface BotDataLoader extends DataLoader, GuidoEventHandler {

  /**
   * Get the discord data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NonNull
  BotLinkable getDiscordUserData(long userId);

  /**
   * Get the discord data for a member
   *
   * @param userId the id of the user to get the data
   * @param guildId the id of the guild where the user is member from
   * @return the data of the user
   */
  @NonNull
  BotLinkable getMemberData(long userId, long guildId);

  /**
   * Get all the member data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NonNull
  Collection<BotLinkable> getDiscordData(long userId);

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
   * Get the max page of the leaderboard in a ladder
   *
   * @param ladder the ladder to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  long maxPageLeaderboard(@NonNull Ladder ladder, int size);

  /**
   * Get the max page of the leaderboard in a stat
   *
   * @param stat the stat to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  long maxPageLeaderboard(@NonNull String stat, int size);

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

  @Override
  BotLinkable getLink(@NonNull LinkableType type, @NonNull ValuesMap identifications);

  @Override
  BotLinkable getLink(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition);

  @Override
  BotLinkable getLinkByRecognition(@NonNull LinkableType type, @NonNull ValuesMap recognition);
}
