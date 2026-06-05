package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.UserLoader;
import me.googas.api.user.UserData;
import me.googas.bot.core.loader.mongo.types.MongoUserData;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class MongoUserLoader extends SimpleMongoLoader implements UserLoader {

  @NonNull @Getter private final MongoLoader loader;
  private final MongoCollection<MongoUserData> collection;

  public MongoUserLoader(@NonNull MongoLoader loader, MongoCollection<MongoUserData> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @NonNull
  public Optional<MongoUserData> getUserData(@NonNull UUID id) {
    MongoUserData match = collection.find(Filters.eq("_id", id)).first();
    return Optional.ofNullable(match);
  }

  public @NonNull MongoUserData create() {
    MongoUserData data = new MongoUserData(UUID.randomUUID());
    collection.insertOne(data);
    return data;
  }

  @Override
  public @NonNull UserData ensureUserData(@NotNull User user) {
    return this.loader
        .getDiscordLinks()
        .ensureByUser(user)
        .getLinkedUserId()
        .flatMap(this::getUserData)
        .orElseThrow();
  }
}
