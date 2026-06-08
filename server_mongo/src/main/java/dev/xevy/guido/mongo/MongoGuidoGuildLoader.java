package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoGuidoGuild;
import dev.xevy.guido.mongo.types.MongoLadder;
import dev.xevy.guido.mongo.types.MongoRankRange;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.server.loader.GuidoGuildLoader;

public class MongoGuidoGuildLoader extends SimpleMongoLoader implements GuidoGuildLoader {

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoGuidoGuild.Document> collection;

  public MongoGuidoGuildLoader(
      @NonNull MongoLoader loader, @NonNull MongoCollection<MongoGuidoGuild.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  private Optional<MongoGuidoGuild> getById(long id) {
    MongoGuidoGuild.Document doc = this.collection.find(Filters.eq("_id", id)).first();
    MongoGuidoGuild match = doc == null ? null : new MongoGuidoGuild(doc, this);
    return Optional.ofNullable(match);
  }

  @NonNull
  private MongoGuidoGuild create(long id) {
    MongoGuidoGuild.Document doc = new MongoGuidoGuild.Document();
    doc.id = id;
    MongoGuidoGuild created = new MongoGuidoGuild(doc, this);
    this.collection.insertOne(doc);
    return created;
  }

  @Override
  public @NonNull me.googas.server.GuidoGuild getGuildOrCreate(long id) {
    return this.getById(id).orElseGet(() -> this.create(id));
  }

  public void setMatchesChannelId(@NonNull MongoGuidoGuild mongoGuidoGuild, long matchesChannelId) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()),
        Updates.set("matchesChannelId", matchesChannelId));
  }

  public void setMatchesCategoryId(MongoGuidoGuild mongoGuidoGuild, long matchesCategoryId) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()),
        Updates.set("matchesCategoryId", matchesCategoryId));
  }

  public void addLadder(
      @NonNull MongoGuidoGuild mongoGuidoGuild, @NonNull MongoLadder.Document ladder) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()), Updates.push("ladders", ladder));
  }

  public void removeLadder(@NonNull MongoGuidoGuild mongoGuidoGuild, int index) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()), Updates.unset("ladders." + index));
  }

  public void addRange(@NonNull MongoGuidoGuild mongoGuidoGuild, MongoRankRange.Document range) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()), Updates.push("ranges", range));
  }

  public void removeRange(@NonNull MongoGuidoGuild mongoGuidoGuild, int index) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()), Updates.unset("ranges." + index));
  }

  public void setWaitingChannelId(@NonNull MongoGuidoGuild mongoGuidoGuild, long waitingChannelId) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()),
        Updates.set("waitingChannelId", waitingChannelId));
  }
}
