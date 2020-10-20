package com.starfishst.bot.api.data;

import com.starfishst.bot.handlers.data.GuidoLadder;
import com.starfishst.guido.api.data.discord.GuildData;
import java.util.Collection;

/** The data of a guild in jda */
public interface BotGuild extends GuildData {

  @Override
  Collection<GuidoLadder> getLadders();
}
