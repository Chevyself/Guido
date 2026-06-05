package me.googas.api.links;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.user.UserData;

public interface LinkableMatcher {
  @NonNull
  Optional<DiscordLinkable> getDiscord(@NonNull Linkable linkable);

  @NonNull
  Optional<MinecraftLinkable> getMinecraftByLinkedUser(@NonNull UUID linkedUserId);

  @NonNull
  Optional<MinecraftLinkable> getMinecraftById(@NonNull UUID id);

  @NonNull
  String getEffectiveName(@NonNull DiscordLinkable discordLinkable);

  @NonNull
  Collection<Linkable> getLinkedAccounts(@NonNull UserData toSee);

  @NonNull
  Optional<UserData> getUserByLink(@NonNull Linkable linkable);

  @NonNull
  Optional<MinecraftLinkable> getMinecraft(DiscordLinkable discord);
}
