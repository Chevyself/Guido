package com.starfishst.bot.api.data.loader;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.guido.api.data.loader.DataLoader;
import org.jetbrains.annotations.NotNull;

/** Loads the data for the bot */
public interface BotDataLoader extends DataLoader {

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
}
