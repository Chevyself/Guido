package dev.xevy.guido.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoMinecraftLinkable;
import dev.xevy.guido.mongo.types.MongoNamedMinecraftLinkable;
import dev.xevy.guido.mongo.util.Options;
import java.util.*;
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
    MongoMinecraftLinkable match = doc == null ? null : new MongoMinecraftLinkable(this, doc);
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
    return new MongoMinecraftLinkable(this, document);
  }

  @Override
  public @NonNull List<MongoNamedMinecraftLinkable> getNicknamesFor(@NonNull Collection<UUID> ids) {
    FindIterable<MongoNamedMinecraftLinkable> iterable =
        this.collection
            .find(Filters.in("_id", ids), MongoNamedMinecraftLinkable.class)
            .projection(
                Projections.fields(Projections.excludeId(), Projections.include("nickname")));
    List<MongoNamedMinecraftLinkable> result = new ArrayList<>(ids.size());
    try (MongoCursor<MongoNamedMinecraftLinkable> cursor = iterable.cursor()) {
      while (cursor.hasNext()) {
        result.add(cursor.next());
      }
    }
    return result;
  }

  public void setLinkedUser(
      @NonNull MongoMinecraftLinkable mongoMinecraftLinkable, @NonNull UserData user) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftLinkable.getId()),
        Updates.set("linkedUserId", user.getId()));
  }

  @NonNull
  public Optional<MongoMinecraftLinkable.Document> update(
      @NonNull MongoMinecraftLinkable mongoMinecraftLinkable,
      @NonNull String nickname,
      @NonNull String ip,
      boolean online) {
    MongoMinecraftLinkable.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftLinkable),
            Updates.combine(
                Updates.set("nickname", nickname),
                Updates.set("ip", ip),
                Updates.set("online", online)),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }
}
