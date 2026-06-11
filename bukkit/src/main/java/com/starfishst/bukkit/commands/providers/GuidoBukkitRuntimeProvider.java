package com.starfishst.bukkit.commands.providers;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class GuidoBukkitRuntimeProvider implements BukkitExtraArgumentProvider<GuidoBukkitRuntime> {
  @NonNull private final GuidoBukkitRuntime runtime;

  public GuidoBukkitRuntimeProvider(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

  @NotNull
  @Override
  public GuidoBukkitRuntime getObject(@NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    return this.runtime;
  }

  @Override
  public @NonNull Class<GuidoBukkitRuntime> getClazz() {
    return GuidoBukkitRuntime.class;
  }
}
