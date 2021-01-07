package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import me.googas.api.loader.UserLoader;
import me.googas.api.user.UserData;
import me.googas.bot.core.user.GuidoUser;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoUserLoader extends SimpleJsongoLoader implements UserLoader {

  public JsongoUserLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  public MongoCollection<Document> users() {
    return this.getCollection("users");
  }

  @Override
  public UserData getUserData(String id) {
    if (id == null) return null;
    return Mongo.get(
        GuidoUser.class,
        this.users(),
        new Document("id", id),
        user -> user.getId().equalsIgnoreCase(id));
  }
}
