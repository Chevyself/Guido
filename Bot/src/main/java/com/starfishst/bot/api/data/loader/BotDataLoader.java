package com.starfishst.bot.api.data.loader;

import com.starfishst.bot.api.data.BotGroup;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
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

  @Override
  @NotNull
  BotGuild getGuildData(long id);

  @Override
  @NotNull
  BotRole getRoleData(long id, long guildId);

  @Override
  @Nullable
  BotUser getUserData(@Nullable String id);

  @Nullable
  BotLinkedData getLinkedData(@NotNull LinkedDataType type, @NotNull ValuesMap identifications);

  @Override
  @NotNull
  Collection<BotLinkedData> getLinks(@NotNull UserData user);

  @Override
  @Nullable
  BotGroup getGroup(@NotNull String id);
}
