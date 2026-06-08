package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoMinecraftLinksLoader;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.api.user.UserData;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftLinkable implements MinecraftLinkable {

  private static final Logger logger = LoggerFactory.getLogger(MongoMinecraftLinkable.class);

  @NonNull private final Document document;
  @NonNull private final MongoMinecraftLinksLoader loader;

  public MongoMinecraftLinkable(
      @NonNull Document document, @NonNull MongoMinecraftLinksLoader loader) {
    this.document = document;
    this.loader = loader;
  }

  @Override
  public @NonNull String getNickname() {
    return this.document.nickname;
  }

  @Override
  public @NonNull String getIp() {
    return this.document.ip;
  }

  @Override
  public boolean isOnline() {
    return this.document.online;
  }

  @Override
  public @NonNull UUID getId() {
    return this.document.id;
  }

  @Override
  public @NonNull Optional<UUID> getLinkedUserId() {
    return Optional.ofNullable(this.document.linkedUserId);
  }

  @Override
  public Stats getStats(@NonNull StatsProvider statsProvider) {
    // TODO
    throw new UnsupportedOperationException("TODO");
  }

  private boolean noLoader(@NonNull Supplier<String> message) {
    logger.log(Level.WARNING, message.get(), new IllegalStateException());
    return false;
  }

  @Override
  public void setLinkedUser(@NonNull UserData user) {
    this.loader.setLinkedUser(this, user);
    this.document.linkedUserId = user.getId();
  }

  public void update(@NonNull String nickname, @NonNull String ip, boolean online) {
    // TODO
    // this.loader.update(this, nickname, ip, online);
  }

  public static class Document {
    @BsonId public UUID id;
    public String nickname;
    public String ip;
    public UUID linkedUserId;
    public boolean online;
  }
}
