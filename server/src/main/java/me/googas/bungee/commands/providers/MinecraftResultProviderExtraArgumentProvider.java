package me.googas.bungee.commands.providers;

import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.providers.type.BungeeExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.guido.mc.MinecraftResultProvider;
import me.googas.bungee.BungeeResultProvider;
import org.jspecify.annotations.NonNull;

public class MinecraftResultProviderExtraArgumentProvider
    implements BungeeExtraArgumentProvider<MinecraftResultProvider> {
  @NonNull private final BungeeResultProvider resultProvider;

  public MinecraftResultProviderExtraArgumentProvider(
      @NonNull BungeeResultProvider resultProvider) {
    this.resultProvider = resultProvider;
  }

  @Override
  public @NonNull MinecraftResultProvider getObject(@NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    return resultProvider;
  }

  @Override
  public @NonNull Class<MinecraftResultProvider> getClazz() {
    return MinecraftResultProvider.class;
  }
}
