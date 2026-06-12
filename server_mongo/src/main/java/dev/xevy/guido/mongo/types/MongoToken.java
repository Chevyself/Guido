package dev.xevy.guido.mongo.types;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.api.utility.UUIDUtils;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoToken implements AuthToken {

  @NonNull private final Document document;

  public MongoToken(@NonNull Document document) {
    this.document = document;
  }

  public @NonNull UUID getId() {
    return this.document.id;
  }

  @Override
  public @NonNull String getToken() {
    return this.document.token;
  }

  @Override
  public @NonNull UUID getUserId() {
    return this.document.userId;
  }

  @Override
  public @NonNull AuthLevel getAuthLevel() {
    return this.document.authLevel;
  }

  public static class Document {
    @NonNull @BsonId public UUID id = UUIDUtils.EMPTY;
    @NonNull public String token = "";
    @NonNull public UUID userId = UUIDUtils.EMPTY;
    @NonNull public AuthLevel authLevel = AuthLevel.NONE;
  }
}
