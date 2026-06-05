package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.MinecraftLinkableLoader;
import me.googas.api.user.UserData;
import me.googas.bot.core.loader.mongo.types.MongoMinecraftLink;

public class MongoMinecraftLinksLoader extends SimpleMongoLoader
    implements MinecraftLinkableLoader {

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoMinecraftLink> collection;

  public MongoMinecraftLinksLoader(
      @NonNull MongoLoader loader, @NonNull MongoCollection<MongoMinecraftLink> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @Override
  public @NonNull Optional<MinecraftLinkable> getByNickname(@NonNull String nickname) {
    MongoMinecraftLink match = this.collection.find(Filters.regex("nickname", nickname)).first();
    if (match != null) {
      match.setLoader(this);
    }
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull Optional<MinecraftLinkable> getByIdRegex(@NonNull String id) {
    MongoMinecraftLink match = this.collection.find(Filters.regex("_id", id)).first();
    if (match != null) {
      match.setLoader(this);
    }
    return Optional.ofNullable(match);
  }

  public void setLinkedUser(
      @NonNull MongoMinecraftLink mongoMinecraftLink, @NonNull UserData user) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftLink.getId()), Updates.set("linkedUserId", user.getId()));
  }
}
