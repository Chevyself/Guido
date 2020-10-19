package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.messages.MessagesProvider;
import com.starfishst.bungee.providers.registry.BungeeProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/** The providers registry for guido */
public class GuidoProvidersRegistry extends BungeeProvidersRegistry {

  /**
   * Create the registry
   *
   * @param messages the messages provider
   */
  public GuidoProvidersRegistry(@NotNull MessagesProvider messages) {
    super(messages);
    this.addProvider(new LocaleFileProvider());
    this.addProvider(new ProxiedOfflinePlayerProvider());
  }
}
