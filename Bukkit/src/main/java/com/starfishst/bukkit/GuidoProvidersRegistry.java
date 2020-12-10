package com.starfishst.bukkit;

import com.starfishst.bukkit.commands.providers.BukkitLocaleFileProvider;
import com.starfishst.bukkit.commands.providers.GameModeProvider;
import com.starfishst.bukkit.messages.MessagesProvider;
import com.starfishst.bukkit.providers.registry.BukkitProvidersRegistry;
import lombok.NonNull;

/** The providers registry for guido */
public class GuidoProvidersRegistry extends BukkitProvidersRegistry {

  /**
   * Create the registry
   *
   * @param messages the provider for messages
   */
  public GuidoProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
    this.providers.add(new BukkitLocaleFileProvider());
    this.providers.add(new GameModeProvider());
  }
}
