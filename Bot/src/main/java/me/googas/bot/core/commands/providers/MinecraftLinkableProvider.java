package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

public class MinecraftLinkableProvider implements JdaArgumentProvider<MinecraftLinkable> {

  @Override
  public @NonNull Class<MinecraftLinkable> getClazz() {
    return MinecraftLinkable.class;
  }

  @Override
  public @NonNull MinecraftLinkable fromString(
      @NonNull String s, @NonNull CommandContext commandContext) throws ArgumentProviderException {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    Linkable linkable =
        loader
            .getLinks()
            .getLinkByRecognition(LinkableType.MINECRAFT, new GuidoValuesMap("nickname", s));
    if (linkable != null) return new MinecraftLinkable(linkable);
    if (s.contains("-")) {
      s = s.replace("-", "");
    }
    linkable = loader.getLinks().getLink(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", s));
    if (linkable != null) return new MinecraftLinkable(linkable);
    throw Lang.getException("invalid.minecraft", Maps.singleton("string", s), commandContext);
  }
}
