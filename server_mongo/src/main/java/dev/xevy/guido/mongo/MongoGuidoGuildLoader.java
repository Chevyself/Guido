package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoGuidoGuild;
import dev.xevy.guido.mongo.types.MongoLadder;
import dev.xevy.guido.mongo.types.MongoRankRange;
import dev.xevy.guido.mongo.util.Options;
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
    MongoGuidoGuild match = doc == null ? null : new MongoGuidoGuild(this, doc);
    return Optional.ofNullable(match);
  }

  @NonNull
  private MongoGuidoGuild create(long id) {
    MongoGuidoGuild.Document doc = new MongoGuidoGuild.Document();
    doc.id = id;
    MongoGuidoGuild created = new MongoGuidoGuild(this, doc);
    this.collection.insertOne(doc);
    return created;
  }

  @Override
  public @NonNull me.googas.server.GuidoGuild getGuildOrCreate(long id) {
    return this.getById(id).orElseGet(() -> this.create(id));
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> setMatchesChannelId(
      @NonNull MongoGuidoGuild mongoGuidoGuild, long matchesChannelId) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.set("matchesChannelId", matchesChannelId),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> setMatchesCategoryId(
      MongoGuidoGuild mongoGuidoGuild, long matchesCategoryId) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.set("matchesCategoryId", matchesCategoryId),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> addLadder(
      @NonNull MongoGuidoGuild mongoGuidoGuild, @NonNull MongoLadder.Document ladder) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.push("ladders", ladder),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> removeLadder(
      @NonNull MongoGuidoGuild mongoGuidoGuild, int index) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.unset("ladders." + index),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> addRange(
      @NonNull MongoGuidoGuild mongoGuidoGuild, MongoRankRange.Document range) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.push("ranges", range),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> removeRange(
      @NonNull MongoGuidoGuild mongoGuidoGuild, int index) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.unset("ranges." + index),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> setWaitingChannelId(
      @NonNull MongoGuidoGuild mongoGuidoGuild, long waitingChannelId) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.set("waitingChannelId", waitingChannelId),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoGuidoGuild.Document> setLadderToJoin(
      @NonNull MongoGuidoGuild mongoGuidoGuild, long voiceId, @NonNull String ladderName) {
    MongoGuidoGuild.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoGuidoGuild.getId()),
            Updates.set("voiceToLadder." + voiceId, ladderName),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }
}
