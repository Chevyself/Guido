package com.starfishst.bukkit.commands.providers;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.starfishst.bukkit.BukkitResultProvider;
import dev.xevy.guido.mc.MinecraftResultProvider;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class MinecraftResultProviderExtraArgumentProvider
    implements BukkitExtraArgumentProvider<MinecraftResultProvider> {
  @NonNull private final BukkitResultProvider resultProvider;

  public MinecraftResultProviderExtraArgumentProvider(
      @NonNull BukkitResultProvider resultProvider) {
    this.resultProvider = resultProvider;
  }

  @NotNull
  @Override
  public MinecraftResultProvider getObject(@NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    return resultProvider;
  }

  @Override
  public @NonNull Class<MinecraftResultProvider> getClazz() {
    return MinecraftResultProvider.class;
  }
}
