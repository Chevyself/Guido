package me.googas.bot.core.loader.mongo.types;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.api.user.UserData;
import me.googas.bot.core.loader.mongo.MongoMinecraftLinksLoader;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftLink implements MinecraftLinkable {

  private static final Logger logger = LoggerFactory.getLogger(MongoMinecraftLink.class);

  @NonNull @BsonId @Getter private final UUID id;
  @NonNull @Getter private final String nickname;
  private UUID linkedUserId;
  private boolean online;
  private transient MongoMinecraftLinksLoader loader;

  public MongoMinecraftLink(
      @NonNull UUID id, @NonNull String nickname, UUID linkedUserId, boolean online) {
    this.id = id;
    this.nickname = nickname;
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

  @Override
  public void setLinkedUser(@NonNull UserData user) {
    if (this.loader == null) {
      logger.log(
          Level.WARNING,
          String.format(
              "Failed to set linked user for Minecraft link %s to use %s, loader has not been set",
              this, user),
          new IllegalStateException());
      return;
    }
    this.loader.setLinkedUser(this, user);
    this.linkedUserId = user.getId();
  }
}
