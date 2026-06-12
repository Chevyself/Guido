package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import dev.xevy.guido.mongo.types.MongoToken;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.TokenLoader;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.api.utility.RandomUtils;
import me.googas.starbox.logging.LoggerFactory;

public class MongoTokenLoader extends SimpleMongoLoader implements TokenLoader {

  @NonNull private static final Logger logger = LoggerFactory.getLogger(MongoTokenLoader.class);

  @NonNull @Getter private final MongoLoader loader;
  private final MongoCollection<MongoToken.Document> collection;

  public MongoTokenLoader(
      @NonNull MongoLoader loader, MongoCollection<MongoToken.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @Override
  @NonNull
  public Optional<AuthToken> getAuthToken(@NonNull String token) {
    MongoToken.Document match = this.collection.find(Filters.eq("token", token)).first();
    MongoToken value = match == null ? null : new MongoToken(match);
    return Optional.ofNullable(value);
  }

  @Override
  public @NonNull Collection<MongoToken> getTokens(@NonNull UUID userId) {
    List<MongoToken> match = new ArrayList<>();
    try (MongoCursor<MongoToken.Document> cursor =
        collection.find(Filters.eq("userId", userId)).cursor()) {
      while (cursor.hasNext()) {
        match.add(new MongoToken(cursor.next()));
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to get groups", e);
    }
    return match;
  }

  @Override
  public @NonNull MongoToken create(@NonNull UUID userId, @NonNull AuthLevel level) {
    MongoToken.Document doc = new MongoToken.Document();
    doc.id = UUID.randomUUID();
    doc.token = RandomUtils.nextString(16);
    doc.userId = userId;
    doc.authLevel = level;
    MongoToken token = new MongoToken(doc);
    collection.insertOne(doc);
    return token;
  }
}
