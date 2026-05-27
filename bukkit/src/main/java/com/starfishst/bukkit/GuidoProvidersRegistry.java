package com.starfishst.bukkit;

import lombok.NonNull;
import me.googas.commands.bukkit.messages.MessagesProvider;
import me.googas.commands.bukkit.providers.registry.BukkitProvidersRegistry;

public class GuidoProvidersRegistry extends BukkitProvidersRegistry {

  public GuidoProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
  }
}
