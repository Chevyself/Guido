package me.googas.api.loader;

import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;

public interface MinecraftMatchLoader extends DataLoader {

  @NonNull
  MinecraftMatch createMatch(
      @NonNull Collection<? extends MinecraftMatchTeam> teams, @NonNull String ladderName);

  @NonNull
  Optional<MinecraftMatch> getByRegexId(@NonNull String pattern);
}
