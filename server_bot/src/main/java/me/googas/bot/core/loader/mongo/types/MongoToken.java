package me.googas.bot.core.loader.mongo.types;

import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoToken implements AuthToken {
  @NonNull @BsonId @Getter private final UUID id;
  @NonNull @Getter private final String token;
  @NonNull @Getter private final UUID userId;
  @NonNull @Getter private final AuthLevel authLevel;

  public MongoToken(
      @NonNull UUID id, @NonNull String token, @NonNull UUID userId, @NonNull AuthLevel authLevel) {
    this.id = id;
    this.token = token;
    this.userId = userId;
    this.authLevel = authLevel;
  }
}
