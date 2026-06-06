package me.googas.api.links.generic;

import com.google.gson.annotations.SerializedName;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.api.user.UserData;

public class ImmutableMinecraftLinkable implements MinecraftLinkable {

  @NonNull
  @Getter
  @SerializedName("_id")
  private final UUID id;

  @NonNull @Getter private final String nickname;
  private final boolean online;
  private final UUID linkedUserId;

  public ImmutableMinecraftLinkable(
      @NonNull UUID id, @NonNull String nickname, boolean online, UUID linkedUserId) {
    this.id = id;
    this.nickname = nickname;
    this.online = online;
    this.linkedUserId = linkedUserId;
  }

  @Override
  public boolean isOnline() {
    return this.online;
  }

  @Override
  public @NonNull Optional<UUID> getLinkedUserId() {
    return Optional.ofNullable(linkedUserId);
  }

  @Override
  public Stats getStats(@NonNull StatsProvider statsProvider) {
    // TODO
    return null;
  }

  @Override
  public void setLinkedUser(@NonNull UserData user) {
    throw new UnsupportedOperationException("Immutable");
  }
}
