package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.messages.MessagesProvider;
import com.starfishst.bungee.providers.registry.BungeeProvidersRegistry;
import lombok.NonNull;

/** The providers registry for guido */
public class GuidoProvidersRegistry extends BungeeProvidersRegistry {

  /**
   * Create the registry
   *
   * @param messages the messages provider
   */
  public GuidoProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
    this.addProvider(new GroupProvider());
    this.addProvider(new BungeeLocaleFileProvider());
    this.addProvider(new JsonClientProvider());
    this.addProvider(new ProxiedOfflinePlayerProvider());
  }
}
