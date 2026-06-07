package me.googas.bot.core.matches.ladder;

import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.bot.GuidoBotRuntime;

public interface PlayableLadder extends Ladder {
  @NonNull
  MinecraftQueue createQueue(@NonNull GuidoBotRuntime runtime);
}
