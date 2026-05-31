package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.user.UserData;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Lang;

public class MinecraftLinkableProvider implements JdaArgumentProvider<MinecraftLinkable> {

  @Override
  public @NonNull Class<MinecraftLinkable> getClazz() {
    return MinecraftLinkable.class;
  }

  @Override
  public @NonNull MinecraftLinkable fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    Linkable linkable =
        loader
            .getLinks()
            .getLinkByRecognition(LinkableType.MINECRAFT, Maps.singleton("nickname", s));
    if (linkable != null) return new MinecraftLinkable(linkable);
    if (s.contains("-")) {
      s = s.replace("-", "");
    }
    linkable = loader.getLinks().getLink(LinkableType.MINECRAFT, Maps.singleton("uuid", s));
    if (linkable != null) return new MinecraftLinkable(linkable);
    try {
      DiscordLinkable discord =
          context.getProvidersRegistry().fromString(s, DiscordLinkable.class, context);
      UserData user = discord.getLinkedUser();
      if (user != null) {
        Linkable link = user.getLink(LinkableType.MINECRAFT);
        if (link != null) return link.requireMinecraftRef();
      }
    } catch (ArgumentProviderException ignored) {
    }
    throw Lang.getException("invalid.minecraft", Maps.singleton("string", s), context);
  }
}
