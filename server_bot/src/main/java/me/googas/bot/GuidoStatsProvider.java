package me.googas.bot;

import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.bot.core.GuidoBotRuntime;

public class GuidoStatsProvider implements StatsProvider {

  @NonNull private final GuidoBotRuntime runtime;

  public GuidoStatsProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Stats getFor(@NonNull MinecraftMatchTeamMember member) {
    return this.runtime
        .getLoader()
        .getStats()
        .getForMinecraftLink(member.getId(), Stats.EMPTY_CONTEXT);
  }
}
