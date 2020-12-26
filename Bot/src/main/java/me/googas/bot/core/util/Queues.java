package me.googas.bot.core.util;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;

/** Static utilities for queues */
public class Queues {

  @NonNull
  public static MinecraftLinkable getToPlayMinecraft(@NonNull Collection<Linkable> links) {
    for (Linkable link : links) {
      if (link.getType() == LinkableType.MINECRAFT) return new MinecraftLinkable(link);
    }
    throw new IllegalArgumentException(links + " does not contain minecraft links");
  }
}
