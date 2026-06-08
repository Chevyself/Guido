package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoMinecraftLinkable;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.MinecraftLinkableLoader;
import me.googas.api.user.UserData;
import org.bson.conversions.Bson;

public class MongoMinecraftLinksLoader extends SimpleMongoLoader
    implements MinecraftLinkableLoader {

  @NonNull public static final String NICKNAME = "nickname";

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoMinecraftLinkable.Document> collection;

  public MongoMinecraftLinksLoader(
      @NonNull MongoLoader loader,
      @NonNull MongoCollection<MongoMinecraftLinkable.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @NonNull
  private Optional<MongoMinecraftLinkable> getBy(@NonNull Bson bson) {
    MongoMinecraftLinkable.Document doc = this.collection.find(bson).first();
    MongoMinecraftLinkable match = doc == null ? null : new MongoMinecraftLinkable(doc, this);
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull Optional<MongoMinecraftLinkable> getByNickname(@NonNull String nickname) {
    return this.getBy(Filters.regex("nickname", nickname));
  }

  @Override
  public @NonNull Optional<MongoMinecraftLinkable> getByIdRegex(@NonNull String id) {
    return this.getBy(Filters.regex("_id", id));
  }

  @Override
  public @NonNull Optional<MongoMinecraftLinkable> getById(@NonNull UUID minecraftId) {
    return this.getBy(Filters.eq("_id", minecraftId));
  }

  @Override
  public @NonNull Optional<MongoMinecraftLinkable> getByLinkedUser(@NonNull UUID linkedUserId) {
    return this.getBy(Filters.eq("linkedUserId", linkedUserId));
  }

  @Override
  public @NonNull MinecraftLinkable updateOrCreate(
      @NonNull UUID minecraftId, @NonNull String nickname, @NonNull String ip, boolean online) {
    Optional<MongoMinecraftLinkable> optional = this.getById(minecraftId);
    if (optional.isPresent()) {
      MongoMinecraftLinkable match = optional.get();
      match.update(nickname, ip, online);
      return match;
    }
    MongoMinecraftLinkable.Document document = new MongoMinecraftLinkable.Document();
    document.id = minecraftId;
    document.nickname = nickname;
    document.ip = ip;
    document.online = online;
    this.collection.insertOne(document);
    return new MongoMinecraftLinkable(document, this);
  }

  public void setLinkedUser(
      @NonNull MongoMinecraftLinkable mongoMinecraftLinkable, @NonNull UserData user) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftLinkable.getId()),
        Updates.set("linkedUserId", user.getId()));
  }
}
