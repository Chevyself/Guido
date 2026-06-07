package me.googas.bot;

import lombok.NonNull;
import me.googas.api.matches.ladder.LadderProvider;
import me.googas.api.stats.StatsProvider;
import me.googas.bot.core.handlers.ranks.RanksProvider;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.server.GuidoServerRuntime;
import me.googas.starbox.events.ListenerManager;

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
  ListenerManager getListeners();

  @NonNull
  GuidoHandlerRegistry getHandlers();
}
