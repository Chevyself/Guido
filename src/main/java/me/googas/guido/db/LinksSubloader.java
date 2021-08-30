package me.googas.guido.db;

import lombok.NonNull;
import me.googas.guido.type.MinecraftLink;
import net.dv8tion.jda.api.entities.User;

public interface LinksSubloader {

  @NonNull
  MinecraftLink getMinecraftLink(@NonNull User user);
}
