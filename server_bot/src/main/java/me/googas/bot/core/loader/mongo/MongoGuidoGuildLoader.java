package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.DiscordRankRange;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.loader.GuidoGuildLoader;
import me.googas.bot.core.loader.mongo.types.MongoGuidoGuild;
import me.googas.bot.core.matches.ladder.GuidoLadder;

public class MongoGuidoGuildLoader extends SimpleMongoLoader implements GuidoGuildLoader {

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoGuidoGuild> collection;

  public MongoGuidoGuildLoader(
      @NonNull MongoLoader loader, @NonNull MongoCollection<MongoGuidoGuild> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  private Optional<MongoGuidoGuild> getById(long id) {
    MongoGuidoGuild match = this.collection.find(Filters.eq("_id", id)).first();
    if (match != null) {
      match.setLoader(this);
    }
    return Optional.ofNullable(match);
  }

  @NonNull
  private MongoGuidoGuild create(long id) {
    MongoGuidoGuild created = new MongoGuidoGuild(id);
    this.collection.insertOne(created);
    created.setLoader(this);
    return created;
  }

  @Override
  public @NonNull GuidoGuild getGuildOrCreate(long id) {
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

  public void addLadder(@NonNull MongoGuidoGuild mongoGuidoGuild, @NonNull GuidoLadder ladder) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()), Updates.push("ladders", ladder));
  }

  public void removeLadder(@NonNull MongoGuidoGuild mongoGuidoGuild, int index) {
    this.collection.updateOne(
        Filters.eq("_id", mongoGuidoGuild.getId()), Updates.unset("ladders." + index));
  }

  public void addRange(@NonNull MongoGuidoGuild mongoGuidoGuild, @NonNull DiscordRankRange range) {
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
