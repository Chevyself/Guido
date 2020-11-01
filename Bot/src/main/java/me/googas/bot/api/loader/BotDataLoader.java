package me.googas.bot.api.loader;

import java.util.Collection;
import me.googas.api.links.LinkedDataType;
import me.googas.api.loader.DataLoader;
import me.googas.api.matches.Ladder;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkedData;
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

  @Override
  @NotNull
  BotGuild getGuildDataOrCreate(long id);

  @Override
  @Nullable
  BotGuild getGuildData(long id);

  @Override
  @NotNull
  BotRole getRoleData(long id, long guildId);

  @Nullable
  BotLinkedData getLinkedData(
      @NotNull LinkedDataType type, @NotNull ValuesMap identifications, boolean equal);

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
}
