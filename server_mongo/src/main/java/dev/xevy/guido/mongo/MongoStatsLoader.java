package dev.xevy.guido.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import dev.xevy.guido.mongo.types.MongoLeaderboardEntry;
import dev.xevy.guido.mongo.types.MongoStats;
import dev.xevy.guido.mongo.types.MongoStatsId;
import dev.xevy.guido.mongo.util.Options;
import java.util.*;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.StatsLoader;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.LeaderboardEntry;
import me.googas.api.stats.Stats;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoStatsLoader extends SimpleMongoLoader implements StatsLoader {

  @NonNull private static final String ID_CONTEXT = "_id.context";
  @NonNull private static final String ID_LINKABLE_ID = "_id.linkableId";
  @NonNull private static final String VALUES_PREFIX = "values.";

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoStats.Document> collection;

  public MongoStatsLoader(
      @NonNull MongoLoader loader, @NonNull MongoCollection<MongoStats.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @Override
  public long maxPageLeaderboard(@NonNull String context, @NonNull Ladder ladder, int limit) {
    if (limit == 0) return 0;
    return this.collection.countDocuments(Filters.eq(ID_CONTEXT, context));
  }

  @Override
  public long maxPageLeaderboard(@NonNull String context, @NonNull String key, int limit) {
    if (limit == 0) return 0;
    return this.collection.countDocuments(Filters.eq(ID_CONTEXT, context));
  }

  public @NonNull Map<Integer, MongoLeaderboardEntry> internalGetLeaderboard(
      @NonNull String context,
      @NonNull String key,
      int page,
      int limit,
      @NonNull Bson... extraProjections) {
    String lookupFieldName = "minecraft";
    String projectDisplay =
        String.format("$%s.%s", lookupFieldName, MongoMinecraftLinksLoader.NICKNAME);
    String value = VALUES_PREFIX + key;
    Bson projection =
        Projections.fields(
            Stream.concat(
                    Stream.of(
                        Projections.excludeId(),
                        Projections.computed("display", projectDisplay),
                        Projections.computed("value", "$" + value)),
                    Arrays.stream(extraProjections))
                .toArray(Bson[]::new));
    AggregateIterable<MongoLeaderboardEntry> aggregate =
        this.collection.aggregate(
            List.of(
                Aggregates.match(Filters.eq(ID_CONTEXT, context)),
                Aggregates.sort(Sorts.descending(value)),
                Aggregates.skip(page * limit),
                Aggregates.limit(limit),
                Aggregates.lookup(
                    MongoLoader.MINECRAFT_LINKS_COLLECTION_NAME,
                    ID_LINKABLE_ID,
                    "_id",
                    lookupFieldName),
                Aggregates.unwind("$" + lookupFieldName),
                Aggregates.project(projection)),
            MongoLeaderboardEntry.class);
    Map<Integer, MongoLeaderboardEntry> map = new LinkedHashMap<>(limit);
    try (MongoCursor<MongoLeaderboardEntry> cursor = aggregate.cursor()) {
      int i = 0;
      while (cursor.hasNext()) {
        map.put((page * limit) + i + 1, cursor.next());
        i++;
      }
    }
    return map;
  }

  @Override
  public @NonNull Map<Integer, ? extends LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull Ladder ladder, int page, int limit) {
    String winsExpression =
        String.format("$%s%s%s", VALUES_PREFIX, ladder.getName(), Stats.LADDER_WINS_SUFFIX);
    String losesExpression =
        String.format("$%s%s%s", VALUES_PREFIX, ladder.getName(), Stats.LADDER_LOSES_SUFFIX);
    return this.internalGetLeaderboard(
        context,
        ladder.getName() + Stats.LADDER_ELO_SUFFIX,
        page,
        limit,
        Projections.computed("wins", winsExpression),
        Projections.computed("loses", losesExpression));
  }

  @Override
  public @NonNull Map<Integer, MongoLeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull String key, int page, int limit) {
    return this.internalGetLeaderboard(context, key, page, limit);
  }

  @NonNull
  private Bson matchMinecraftLink(@NonNull UUID minecraftLinkId, @NonNull String context) {
    return Filters.and(
        Filters.eq(ID_LINKABLE_ID, minecraftLinkId), Filters.eq(ID_CONTEXT, context));
  }

  @NonNull
  private Optional<MongoStats> getForMinecraftLinkById(
      @NonNull UUID minecraftLinkId, @NonNull String context) {
    MongoStats.Document document =
        this.collection.find(this.matchMinecraftLink(minecraftLinkId, context)).first();
    MongoStats match = document == null ? null : new MongoStats(this, document);
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull MongoStats getForMinecraftLink(@NonNull UUID id, @NonNull String context) {
    return this.getForMinecraftLinkById(id, context)
        .orElseGet(
            () -> {
              MongoStatsId.Document idDoc = new MongoStatsId.Document();
              idDoc.linkableId = id;
              idDoc.context = context;
              MongoStats.Document doc = new MongoStats.Document();
              doc.id = idDoc;
              this.collection.insertOne(doc);
              return new MongoStats(this, doc);
            });
  }

  @Override
  public void saveForMinecraftLink(
      @NonNull UUID id, @NonNull String context, @NonNull Map<String, Double> stats) {
    this.getForMinecraftLink(id, context).increaseAll(stats);
  }

  @NonNull
  public Optional<MongoStats.Document> increase(
      @NonNull MongoStats mongoStats, @NonNull String key, double value) {
    MongoStatsId id = mongoStats.getId();
    MongoStats.Document doc =
        this.collection.findOneAndUpdate(
            this.matchMinecraftLink(id.getLinkableId(), id.getContext()),
            Updates.inc(VALUES_PREFIX + key, value));
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoStats.Document> increaseAll(
      @NonNull MongoStats mongoStats, @NonNull Map<String, Double> stats) {
    List<Bson> updates =
        stats.entrySet().stream()
            .map(entry -> Updates.inc(VALUES_PREFIX + entry.getKey(), entry.getValue()))
            .toList();
    MongoStatsId id = mongoStats.getId();
    MongoStats.Document doc =
        this.collection.findOneAndUpdate(
            this.matchMinecraftLink(id.getLinkableId(), id.getContext()), Updates.combine(updates));
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoStats.Document> increase(
      MongoStats mongoStats, @NonNull String key, double value, double base) {
    MongoStatsId id = mongoStats.getId();

    String field = VALUES_PREFIX + key;

    MongoStats.Document doc =
        this.collection.findOneAndUpdate(
            this.matchMinecraftLink(id.getLinkableId(), id.getContext()),
            List.of(
                Aggregates.set(
                    new Field<>(
                        field,
                        new Document(
                            "$add",
                            List.of(new Document("$ifNull", List.of("$" + field, base)), value))))),
            Options.RETURN_AFTER);

    return Optional.ofNullable(doc);
  }
}
