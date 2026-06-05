package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.TokenLoader;
import me.googas.api.token.AuthToken;
import me.googas.bot.core.loader.mongo.types.MongoToken;
import me.googas.starbox.logging.LoggerFactory;

public class MongoTokenLoader extends SimpleMongoLoader implements TokenLoader {

  @NonNull private static final Logger logger = LoggerFactory.getLogger(MongoTokenLoader.class);

  @NonNull @Getter private final MongoLoader loader;
  private final MongoCollection<MongoToken> collection;

  public MongoTokenLoader(@NonNull MongoLoader loader, MongoCollection<MongoToken> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @Override
  @NonNull
  public Optional<AuthToken> getAuthToken(@NonNull String token) {
    MongoToken match = this.collection.find(Filters.eq("token", token)).first();
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull Collection<MongoToken> getTokens(@NonNull UUID userId) {
    List<MongoToken> match = new ArrayList<>();
    try (MongoCursor<MongoToken> cursor = collection.find(Filters.eq("userId", userId)).cursor()) {
      while (cursor.hasNext()) {
        match.add(cursor.next());
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to get groups", e);
    }
    return match;
  }
}
