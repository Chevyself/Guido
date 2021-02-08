package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.economy.AbstractBank;
import me.googas.api.loader.BanksLoader;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoBanksLoader extends SimpleJsongoLoader implements BanksLoader {
  public JsongoBanksLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  public MongoCollection<Document> banks() {
    return this.getCollection("banks");
  }

  @Nullable
  @Override
  public AbstractBank getBank(@NonNull String id) {
    return Mongo.get(
        AbstractBank.class, this.banks(), new Document("id", id), bank -> bank.getId().equals(id));
  }

  @Override
  public boolean delete(@NonNull String id) {
    AbstractBank bank = this.getBank(id);
    if (bank != null) {
      bank.unload(false);
      return Mongo.delete(this.banks(), new Document("id", id));
    }
    return false;
  }
}
