package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

public class LinkableProvider implements JdaArgumentProvider<Linkable> {
  @Override
  public @NonNull Class<Linkable> getClazz() {
    return Linkable.class;
  }

  @Override
  public @NonNull Linkable fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    try {
      MinecraftLinkable minecraft = commandContext.get(s, MinecraftLinkable.class, commandContext);
      if (minecraft != null) return minecraft.validated();

    } catch (ArgumentProviderException ignored) {
    }
    try {
      DiscordLinkable discordLinkable =
          commandContext.get(s, DiscordLinkable.class, commandContext);
      if (discordLinkable != null) return discordLinkable.validated();
    } catch (ArgumentProviderException ignored) {

    }
    throw Lang.getException("invalid.link", Maps.singleton("string", s), commandContext);
  }
}
