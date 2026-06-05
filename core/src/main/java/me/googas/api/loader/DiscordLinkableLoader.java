package me.googas.api.loader;

import lombok.NonNull;
import me.googas.api.links.DiscordLinkable;
import net.dv8tion.jda.api.entities.User;

public interface DiscordLinkableLoader extends DataLoader {

  @NonNull
  DiscordLinkable ensureByUser(@NonNull User user);
}
