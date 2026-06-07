package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoMinecraftLink;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.MinecraftLinkableLoader;
import me.googas.api.user.UserData;

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

  @Override
  public @NonNull Optional<MongoMinecraftLink> getById(@NonNull UUID minecraftId) {
    MongoMinecraftLink match = this.collection.find(Filters.eq("_id", minecraftId)).first();
    if (match != null) {
      match.setLoader(this);
    }
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull Optional<MinecraftLinkable> getByLinkedUser(@NonNull UUID linkedUserId) {
    MongoMinecraftLink match =
        this.collection.find(Filters.eq("linkedUserId", linkedUserId)).first();
    if (match != null) {
      match.setLoader(this);
    }
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull MinecraftLinkable updateOrCreate(
      @NonNull UUID minecraftId, @NonNull String nickname, @NonNull String ip, boolean online) {
    Optional<MongoMinecraftLink> optional = this.getById(minecraftId);
    if (optional.isPresent()) {
      MongoMinecraftLink match = optional.get();
      match.update(nickname, ip, online);
      return match;
    }
    MongoMinecraftLink link = new MongoMinecraftLink(minecraftId, nickname, ip, null, online);
    link.setLoader(this);
    return link;
  }

  public void setLinkedUser(
      @NonNull MongoMinecraftLink mongoMinecraftLink, @NonNull UserData user) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftLink.getId()), Updates.set("linkedUserId", user.getId()));
  }
}
