package com.starfishst.bot.api.data;

import com.starfishst.bot.handlers.data.GuidoLadder;
import com.starfishst.guido.api.data.discord.GuildData;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/** The data of a guild in jda */
public interface BotGuild extends GuildData {

  @NotNull
  @Override
  Collection<GuidoLadder> getLadders();
}
