package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.bot.Guido;
import me.googas.bot.api.types.links.BotLinkable;
import me.googas.bot.api.types.loader.BotDataLoader;
import me.googas.bot.core.GuidoValuesMap;
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
    BotDataLoader loader = Guido.getDataLoader();
    BotLinkable linkable =
        loader.getLinkByRecognition(LinkableType.MINECRAFT, new GuidoValuesMap("nickname", s));
    if (linkable != null) return new MinecraftLinkable(linkable);
    if (s.contains("-")) {
      s = s.replace("-", "");
    }
    linkable = loader.getLink(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", s));
    if (linkable != null) return new MinecraftLinkable(linkable);
    throw Lang.getException("invalid.minecraft", Maps.singleton("string", s), commandContext);
  }
}
