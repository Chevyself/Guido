package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.utility.Maps;
import me.googas.bot.core.util.Lang;

public class LinkableProvider implements JdaArgumentProvider<Linkable> {
  @Override
  public @NonNull Class<Linkable> getClazz() {
    return Linkable.class;
  }

  @Override
  public @NonNull Linkable fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    try {
      MinecraftLinkable minecraft =
          commandContext
              .getProvidersRegistry()
              .fromString(s, MinecraftLinkable.class, commandContext);
      return minecraft.validated();

    } catch (ArgumentProviderException ignored) {
    }
    try {
      DiscordLinkable discordLinkable =
          commandContext
              .getProvidersRegistry()
              .fromString(s, DiscordLinkable.class, commandContext);
      return discordLinkable.validated();
    } catch (ArgumentProviderException ignored) {

    }
    throw Lang.getException("invalid.link", Maps.singleton("string", s), commandContext);
  }
}
