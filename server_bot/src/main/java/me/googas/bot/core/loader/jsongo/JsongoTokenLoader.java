package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.Collection;
import lombok.NonNull;
import me.googas.api.loader.TokenLoader;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoTokenLoader extends SimpleJsongoLoader implements TokenLoader {

  public JsongoTokenLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  public MongoCollection<Document> tokens() {
    return this.getCollection("tokens");
  }

  @Override
  public AuthToken getAuthToken(@NonNull String token) {
    return Mongo.get(
        AuthToken.class,
        this.tokens(),
        new Document("token", token),
        cacheToken -> cacheToken.getToken().equals(token));
  }

  @NonNull
  @Override
  public Collection<AuthToken> getTokens(@NonNull UserData user) {
    return Mongo.getMany(
        AuthToken.class, this.tokens(), new Document("user", user.getId()), null, -1, -1);
  }
}
