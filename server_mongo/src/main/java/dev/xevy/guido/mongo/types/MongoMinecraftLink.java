package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoMinecraftLinksLoader;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.api.user.UserData;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftLink implements MinecraftLinkable {

  private static final Logger logger = LoggerFactory.getLogger(MongoMinecraftLink.class);

  @NonNull @BsonId @Getter private final UUID id;
  @NonNull @Getter private final String nickname;
  @NonNull @Getter private final String ip;
  @NonNull @Getter private UUID linkedUserId;
  private boolean online;
  private transient MongoMinecraftLinksLoader loader;

  public MongoMinecraftLink(
      @NonNull UUID id,
      @NonNull String nickname,
      @NonNull String ip,
      UUID linkedUserId,
      boolean online) {
    this.id = id;
    this.nickname = nickname;
    this.ip = ip;
    this.linkedUserId = linkedUserId;
    this.online = online;
  }

  @NonNull
  public MongoMinecraftLink setLoader(@NonNull MongoMinecraftLinksLoader loader) {
    this.loader = loader;
    return this;
  }

  @Override
  public boolean isOnline() {
    return online;
  }

  @Override
  public @NonNull Optional<UUID> getLinkedUserId() {
    return Optional.ofNullable(linkedUserId);
  }

  @Override
  public Stats getStats(@NonNull StatsProvider statsProvider) {
    // TODO
    throw new UnsupportedOperationException("TODO");
  }

  private boolean noLoader(@NonNull Supplier<String> message) {
    if (this.loader == null) return true;
    logger.log(Level.WARNING, message.get(), new IllegalStateException());
    return false;
  }

  @Override
  public void setLinkedUser(@NonNull UserData user) {
    if (noLoader(
        () ->
            String.format(
                "Failed to set linked user for Minecraft link %s to use %s, loader has not been set",
                this, user))) {
      return;
    }
    this.loader.setLinkedUser(this, user);
    this.linkedUserId = user.getId();
  }

  public void update(@NonNull String nickname, @NonNull String ip, boolean online) {
    if (noLoader(
        () -> String.format("Failed to update Minecraft link %s, loader has not been set", this))) {
      return;
    }
    // TODO
    // this.loader.update(this, nickname, ip, online);
  }
}
