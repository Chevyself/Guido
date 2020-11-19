package me.googas.bot.api.loader;

import java.util.Collection;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.DataLoader;
import me.googas.api.matches.Ladder;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkable;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.api.types.BotRole;
import me.googas.bot.core.handlers.GuidoEventHandler;
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
  BotLinkable getDiscordUserData(long userId);

  /**
   * Get the discord data for a member
   *
   * @param userId the id of the user to get the data
   * @param guildId the id of the guild where the user is member from
   * @return the data of the user
   */
  @NotNull
  BotLinkable getMemberData(long userId, long guildId);

  /**
   * Get all the member data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @NotNull
  Collection<BotLinkable> getDiscordData(long userId);

  @Override
  @NotNull
  BotGuild getGuildDataOrCreate(long id);

  @Override
  @Nullable
  BotGuild getGuildData(long id);

  /**
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @NotNull
  BotRole getRoleData(long id, long guildId);

  @Nullable
  BotLinkable getLinkedData(@NotNull LinkableType type, @NotNull ValuesMap identifications);

  @Override
  @Nullable
  BotGroup getGroup(@NotNull String id);

  @Override
  @Nullable
  BotMatch getMatch(@NotNull String id);

  /**
   * Get the max page of the leaderboard in a ladder
   *
   * @param ladder the ladder to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  long maxPageLeaderboard(@NotNull Ladder ladder, int size);

  /**
   * Get the max page of the leaderboard in a stat
   *
   * @param stat the stat to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  long maxPageLeaderboard(@NotNull String stat, int size);
}
