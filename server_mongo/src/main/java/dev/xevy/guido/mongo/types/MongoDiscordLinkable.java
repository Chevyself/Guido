package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoDiscordLinksLoader;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.DiscordLinkable;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.api.user.UserData;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoDiscordLinkable implements DiscordLinkable {

  private static final Logger logger = LoggerFactory.getLogger(MongoDiscordLinkable.class);

  @Getter @BsonId private final long id;
  @NonNull private UUID linkedUserId;
  private transient MongoDiscordLinksLoader loader;

  public MongoDiscordLinkable(long id, @NonNull UUID linkedUserId) {
    this.id = id;
    this.linkedUserId = linkedUserId;
  }

  @Override
  public @NonNull Optional<UUID> getLinkedUserId() {
    return Optional.of(linkedUserId);
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
              "Failed to set linked user for Discord link %s to use %s, loader has not been set",
              this, user),
          new IllegalStateException());
      return;
    }
    this.loader.setLinkedUser(this, user);
    this.linkedUserId = user.getId();
  }

  @NonNull
  public MongoDiscordLinkable setLoader(@NonNull MongoDiscordLinksLoader loader) {
    this.loader = loader;
    return this;
  }
}
