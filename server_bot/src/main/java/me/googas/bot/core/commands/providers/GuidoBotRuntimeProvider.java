package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.bot.core.GuidoBotRuntime;
import org.jetbrains.annotations.NotNull;

public class GuidoBotRuntimeProvider implements JdaExtraArgumentProvider<GuidoBotRuntime> {

  @NonNull private final GuidoBotRuntime runtime;

  public GuidoBotRuntimeProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @NotNull
  @Override
  public GuidoBotRuntime getObject(@NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    return runtime;
  }

  @Override
  public @NonNull Class<GuidoBotRuntime> getClazz() {
    return GuidoBotRuntime.class;
  }
}
