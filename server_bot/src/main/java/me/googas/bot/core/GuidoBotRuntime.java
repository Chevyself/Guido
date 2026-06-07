package me.googas.bot.core;

import lombok.NonNull;
import me.googas.api.matches.ladder.LadderProvider;
import me.googas.api.stats.StatsProvider;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.GuidoJdaConnection;
import me.googas.bot.GuidoJdaProvider;
import me.googas.bot.core.handlers.ranks.RanksProvider;
import me.googas.server.GuidoServerRuntime;
import me.googas.server.loader.GuidoLoader;

public interface GuidoBotRuntime extends GuidoServerRuntime {
  @NonNull
  GuidoJdaConnection getJdaConnection();

  @NonNull
  GuidoJdaProvider getBotJda();

  @NonNull
  LadderProvider getLadderProvider();

  @NonNull
  RanksProvider getRanksProvider();

  @NonNull
  StatsProvider getStatsProvider();

  @NonNull
  GuidoLoader getLoader();

  @NonNull
  GuidoHandlerRegistry getHandlers();
}
