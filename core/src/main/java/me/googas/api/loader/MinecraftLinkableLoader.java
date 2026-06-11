package me.googas.api.loader;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.links.NamedMinecraftLinkable;

public interface MinecraftLinkableLoader extends DataLoader {
  @NonNull
  Optional<? extends MinecraftLinkable> getByNickname(@NonNull String nickname);

  @NonNull
  Optional<? extends MinecraftLinkable> getByIdRegex(@NonNull String id);

  @NonNull
  Optional<? extends MinecraftLinkable> getById(@NonNull UUID minecraftId);

  @NonNull
  Optional<? extends MinecraftLinkable> getByLinkedUser(@NonNull UUID linkedUserId);

  @NonNull
  MinecraftLinkable updateOrCreate(
      @NonNull UUID minecraftId, @NonNull String nickname, @NonNull String ip, boolean online);

  @NonNull
  List<? extends NamedMinecraftLinkable> getNicknamesFor(@NonNull Collection<UUID> ids);
}
