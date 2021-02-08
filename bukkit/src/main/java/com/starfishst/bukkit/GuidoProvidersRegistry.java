package com.starfishst.bukkit;

import com.starfishst.commands.bukkit.messages.MessagesProvider;
import com.starfishst.commands.bukkit.providers.registry.BukkitProvidersRegistry;
import lombok.NonNull;

public class GuidoProvidersRegistry extends BukkitProvidersRegistry {

  public GuidoProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
  }
}
