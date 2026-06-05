package me.googas.api.loader;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;

public interface MinecraftLinkableLoader extends DataLoader {
  @NonNull
  Optional<MinecraftLinkable> getByNickname(@NonNull String nickname);

  @NonNull
  Optional<MinecraftLinkable> getByIdRegex(@NonNull String id);
}
