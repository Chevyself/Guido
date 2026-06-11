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

import java.util.UUID;

public class MinecraftMatchProvider implements JdaArgumentProvider<MinecraftMatch> {

  @NonNull private final GuidoBotRuntime runtime;

  public MinecraftMatchProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @NotNull
  @Override
  public MinecraftMatch fromString(@NonNull String s, @NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    try {
      UUID uuid = UUID.fromString(s);
      return runtime
              .getLoader()
              .getMinecraftMatches()
              .getById(uuid)
              .orElseThrow(
                      () -> Lang.getException("invalid.match", Maps.singleton("string", s), commandContext));

    } catch (IllegalArgumentException e) {
      throw Lang.getException("invalid.uuid", Maps.singleton("string", s), commandContext);
    }
  }

  @Override
  public @NonNull Class<MinecraftMatch> getClazz() {
    return MinecraftMatch.class;
  }
}
