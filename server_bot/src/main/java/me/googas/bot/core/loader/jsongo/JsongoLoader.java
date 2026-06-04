package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.core.loader.GuidoLoader;

/**
 * A data loader that uses both mongo and json
 *
 * <h1>IMPORTANT Mongo does not support dots '.' in their field names!!</h1>
 */
public class JsongoLoader implements GuidoLoader {

  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;
  @NonNull @Getter private final JsongoGroupsLoader groups = new JsongoGroupsLoader(this);
  @NonNull @Getter private final JsongoLinksLoader links = new JsongoLinksLoader(this);
  @NonNull @Getter private final JsongoMatchesLoader matches = new JsongoMatchesLoader(this);

  @NonNull @Getter
  private final JsongoPunishmentLoader punishments = new JsongoPunishmentLoader(this);

  @NonNull @Getter private final JsongoTeamLoader teams = new JsongoTeamLoader(this);
  @NonNull @Getter private final JsongoTokenLoader tokens = new JsongoTokenLoader(this);
  @NonNull @Getter private final JsongoUserLoader users = new JsongoUserLoader(this);

  /**
   * Create the mongo data loader
   *
   * @param uri the mongo uri to connect
   * @param databaseName the name of the database
   */
  public JsongoLoader(@NonNull String uri, @NonNull String databaseName) {
    this.client = MongoClients.create(uri);
    this.database = this.client.getDatabase(databaseName);
  }

  @Override
  public void onDisable() {
    this.client.close();
  }
}
