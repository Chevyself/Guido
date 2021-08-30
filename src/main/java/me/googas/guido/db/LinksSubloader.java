package me.googas.guido.db;

import lombok.NonNull;
import me.googas.guido.type.MinecraftLink;
import me.googas.lazy.Subloader;
import net.dv8tion.jda.api.entities.User;

public interface LinksSubloader extends Subloader {

  @NonNull
  MinecraftLink getMinecraftLink(@NonNull User user);
}
