package com.starfishst.bot.commands.providers;

import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.providers.registry.JdaProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/** The providers registry for guido */
public class GuidoProvidersRegistry extends JdaProvidersRegistry {

  /**
   * Create the providers registry
   *
   * @param messages the messages provider for default providers
   */
  public GuidoProvidersRegistry(@NotNull MessagesProvider messages) {
    super(messages);
    this.addProvider(new AuthLevelProvider());
    this.addProvider(new GuildDataProvider());
    this.addProvider(new GuidoUserProvider());
    this.addProvider(new UserDataSenderProvider());
    this.addProvider(new LadderProvider());
    this.addProvider(new LocaleFileProvider());
  }
}
