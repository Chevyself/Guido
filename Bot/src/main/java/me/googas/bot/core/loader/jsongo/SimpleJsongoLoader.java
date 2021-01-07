package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import me.googas.api.loader.DataLoader;
import org.bson.Document;

public class SimpleJsongoLoader implements DataLoader {

  @NonNull private final JsongoLoader loader;

  public SimpleJsongoLoader(@NonNull JsongoLoader loader) {
    this.loader = loader;
  }

  @NonNull
  protected MongoCollection<Document> getCollection(@NonNull String name) {
    return this.loader.getDatabase().getCollection(name);
  }

  @Override
  public @NonNull JsongoLoader getLoader() {
    return this.loader;
  }
}
