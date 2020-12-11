package me.googas.bot.core.commands.providers;

import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.providers.registry.JdaProvidersRegistry;
import lombok.NonNull;

/** The providers registry for guido */
public class GuidoProvidersRegistry extends JdaProvidersRegistry {

  /**
   * Create the providers registry
   *
   * @param messages the messages provider for default providers
   */
  public GuidoProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
    this.addProvider(new AuthLevelProvider());
    this.addProvider(new GuildDataProvider());
    this.addProvider(new GuidoUserProvider());
    this.addProvider(new UserDataSenderProvider());
    this.addProvider(new LadderProvider());
    this.addProvider(new LocaleFileProvider());
    this.addProvider(new TeamDataProvider());
    this.addProvider(new MatchProvider());
  }
}
