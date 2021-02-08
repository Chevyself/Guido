package me.googas.bungee.commands.providers;

import com.starfishst.commands.bungee.messages.MessagesProvider;
import com.starfishst.commands.bungee.providers.registry.BungeeProvidersRegistry;
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
    this.addProvider(new ProxiedOfflinePlayerProvider());
  }
}
