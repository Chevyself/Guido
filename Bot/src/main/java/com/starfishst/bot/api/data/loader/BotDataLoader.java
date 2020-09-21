package com.starfishst.bot.api.data.loader;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.UnlinkedMember;
import com.starfishst.guido.api.data.loader.DataLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads the data for the bot */
public interface BotDataLoader extends DataLoader, GuidoEventHandler {

  @Override
  @NotNull
  BotGuild getGuildData(long id);

  @Override
  @NotNull
  BotMember getMemberData(long id, long guildId);

  @Override
  @NotNull
  BotRole getRoleData(long id, long guildId);

  @Override
  @NotNull
  BotUser getUserData(long id);

  @Override
  @Nullable
  BotMember getMemberByLink(long guild, @NotNull String key, @NotNull String value);

  @Override
  void deleteUnlinked(@NotNull UnlinkedMember member);
}
