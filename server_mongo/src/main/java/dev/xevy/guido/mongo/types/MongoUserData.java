package dev.xevy.guido.mongo.types;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.user.UserData;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoUserData implements UserData {
  @NonNull private final Document document;

  public MongoUserData(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public @NonNull UUID getId() {
    return document.id;
  }

  public static class Document {
    @BsonId public UUID id;
  }
}
