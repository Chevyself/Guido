package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.xevy.guido.mongo.types.MongoUserData;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.UserLoader;
import me.googas.api.user.UserData;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class MongoUserLoader extends SimpleMongoLoader implements UserLoader {

  @NonNull @Getter private final MongoLoader loader;
  private final MongoCollection<MongoUserData.Document> collection;

  public MongoUserLoader(
      @NonNull MongoLoader loader, MongoCollection<MongoUserData.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @Override
  public @NonNull Optional<MongoUserData> getById(@NonNull UUID id) {
    MongoUserData.Document doc = collection.find(Filters.eq("_id", id)).first();
    MongoUserData match = doc == null ? null : new MongoUserData(doc);
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull MongoUserData create() {
    MongoUserData.Document doc = new MongoUserData.Document();
    doc.id = UUID.randomUUID();
    collection.insertOne(doc);
    return new MongoUserData(doc);
  }

  @Override
  public @NonNull UserData ensureUserData(@NotNull User user) {
    return this.loader
        .getDiscordLinks()
        .ensureByUser(user)
        .getLinkedUserId()
        .flatMap(this::getById)
        .orElseThrow();
  }
}
