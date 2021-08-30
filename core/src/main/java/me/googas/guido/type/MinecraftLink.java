package me.googas.guido.type;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.net.cache.Catchable;
import net.dv8tion.jda.api.entities.User;

/** Represents a linked minecraft account to a Discord user */
public interface MinecraftLink extends Catchable {

  @NonNull
  User getUser();

  @NonNull
  Optional<UUID> getMinecraftUniqueId();
}
