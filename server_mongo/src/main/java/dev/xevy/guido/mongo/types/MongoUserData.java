package dev.xevy.guido.mongo.types;

import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.user.UserData;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoUserData implements UserData {
  @NonNull @BsonId @Getter private final UUID id;

  public MongoUserData(@NonNull UUID id) {
    this.id = id;
  }
}
