package me.googas.guido.commands.providers;

import lombok.NonNull;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaExtraArgumentProvider;
import me.googas.guido.GuidoBot;
import me.googas.guido.db.LinksSubloader;
import me.googas.guido.type.MinecraftLink;

public class MinecraftLinkProvider implements JdaExtraArgumentProvider<MinecraftLink> {
  @Override
  public @NonNull MinecraftLink getObject(@NonNull CommandContext context) {
    return GuidoBot.getLoader()
        .getSubloader(LinksSubloader.class)
        .getMinecraftLink(context.getSender());
  }

  @Override
  public @NonNull Class<MinecraftLink> getClazz() {
    return MinecraftLink.class;
  }
}
