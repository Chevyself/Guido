package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.utility.Maps;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.util.Lang;
import org.jetbrains.annotations.NotNull;

public class MinecraftMatchProvider implements JdaArgumentProvider<MinecraftMatch> {

  @NonNull private final GuidoBotRuntime runtime;

  public MinecraftMatchProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @NotNull
  @Override
  public MinecraftMatch fromString(@NonNull String s, @NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    return runtime
        .getLoader()
        .getMinecraftMatches()
        .getByRegexId(s)
        .orElseThrow(
            () -> Lang.getException("invalid.match", Maps.singleton("string", s), commandContext));
  }

  @Override
  public @NonNull Class<MinecraftMatch> getClazz() {
    return MinecraftMatch.class;
  }
}
