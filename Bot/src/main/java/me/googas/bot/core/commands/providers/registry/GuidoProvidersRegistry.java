package me.googas.bot.core.commands.providers.registry;

import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.providers.registry.JdaProvidersRegistry;
import lombok.NonNull;
import me.googas.bot.core.commands.providers.AuthLevelProvider;
import me.googas.bot.core.commands.providers.DiscordGuildLinkableProvider;
import me.googas.bot.core.commands.providers.DiscordLinkableProvider;
import me.googas.bot.core.commands.providers.GuidoUserProvider;
import me.googas.bot.core.commands.providers.GuildDataProvider;
import me.googas.bot.core.commands.providers.LadderProvider;
import me.googas.bot.core.commands.providers.LinkableProvider;
import me.googas.bot.core.commands.providers.LocaleFileProvider;
import me.googas.bot.core.commands.providers.MatchProvider;
import me.googas.bot.core.commands.providers.MinecraftLinkableProvider;
import me.googas.bot.core.commands.providers.MultipleTeamProvider;
import me.googas.bot.core.commands.providers.UserDataSenderProvider;

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
    this.addProvider(new DiscordGuildLinkableProvider());
    this.addProvider(new DiscordLinkableProvider());
    this.addProvider(new GuidoUserProvider());
    this.addProvider(new GuildDataProvider());
    this.addProvider(new LadderProvider());
    this.addProvider(new LinkableProvider());
    this.addProvider(new LocaleFileProvider());
    this.addProvider(new MatchProvider());
    this.addProvider(new MinecraftLinkableProvider());
    this.addProvider(new MultipleTeamProvider());
    this.addProvider(new UserDataSenderProvider());
    this.addProvider(new UserDataSenderProvider());
  }
}
