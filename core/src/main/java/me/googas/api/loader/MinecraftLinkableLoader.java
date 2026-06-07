package me.googas.api.loader;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;

public interface MinecraftLinkableLoader extends DataLoader {
  @NonNull
  Optional<MinecraftLinkable> getByNickname(@NonNull String nickname);

  @NonNull
  Optional<MinecraftLinkable> getByIdRegex(@NonNull String id);

  @NonNull
  Optional<? extends MinecraftLinkable> getById(@NonNull UUID minecraftId);

  @NonNull
  Optional<MinecraftLinkable> getByLinkedUser(@NonNull UUID linkedUserId);

  @NonNull
  MinecraftLinkable updateOrCreate(
      @NonNull UUID minecraftId, @NonNull String nickname, @NonNull String ip, boolean online);
}
