package me.googas.api.matches.minecraft;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.Loader;
import me.googas.api.matches.MatchTeamMember;

public interface MinecraftMatchTeamMember extends MatchTeamMember {

  @NonNull
  UUID getId();

  @NonNull
  default Optional<? extends MinecraftLinkable> getLinkable(@NonNull Loader loader) {
    return loader.getMinecraftLinks().getById(this.getId());
  }
}
