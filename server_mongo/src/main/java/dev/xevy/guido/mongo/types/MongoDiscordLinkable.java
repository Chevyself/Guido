package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoDiscordLinksLoader;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.links.DiscordLinkable;
import me.googas.api.user.UserData;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoDiscordLinkable implements DiscordLinkable {

  private static final Logger logger = LoggerFactory.getLogger(MongoDiscordLinkable.class);

  @NonNull private final Document document;
  @NonNull private final MongoDiscordLinksLoader loader;

  public MongoDiscordLinkable(@NonNull Document document, @NonNull MongoDiscordLinksLoader loader) {
    this.document = document;
    this.loader = loader;
  }

  @Override
  public @NonNull Optional<UUID> getLinkedUserId() {
    return Optional.of(this.document.linkedUserId);
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
    this.document.linkedUserId = user.getId();
  }

  @Override
  public long getId() {
    return this.document.id;
  }

  public static class Document {
    @BsonId public long id;
    public UUID linkedUserId;
  }
}
