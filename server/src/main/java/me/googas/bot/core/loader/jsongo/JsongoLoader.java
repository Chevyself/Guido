package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.group.GroupUnloadedEvent;
import me.googas.api.events.links.LinkableUnloadedEvent;
import me.googas.api.events.match.MatchUnloadedEvent;
import me.googas.api.events.match.team.TeamDataUnloadedEvent;
import me.googas.api.events.punishment.PunishmentUnloadedEvent;
import me.googas.api.events.token.AuthTokenUnloadedEvent;
import me.googas.api.events.user.UserUnloadedDataEvent;
import me.googas.api.links.Linkable;
import me.googas.api.punishment.Punishment;
import me.googas.bot.api.events.data.guild.GuidoGuildUnloadedEvent;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import org.bson.Document;

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
  @NonNull @Getter private final JsonLadderLoader ladders = new JsonLadderLoader(this);

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

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotGuildUnloaded(@NonNull GuidoGuildUnloadedEvent event) {
    Mongo.save(
        this.database.getCollection("guilds"),
        new Document("id", event.getData().getId()),
        event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchUnloaded(@NonNull MatchUnloadedEvent event) {
    Mongo.save(
        this.database.getCollection("matches"),
        new Document("id", event.getAbstractMatch().getId()),
        event.getAbstractMatch());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamDataUnloaded(@NonNull TeamDataUnloadedEvent event) {
    Mongo.save(
        this.database.getCollection("teams"),
        new Document("id", event.getData().getId()),
        event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotUserUnloaded(@NonNull UserUnloadedDataEvent event) {
    Mongo.save(
        this.database.getCollection("users"),
        new Document("id", event.getData().getId()),
        event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onGroupUnloadedEvent(@NonNull GroupUnloadedEvent event) {
    Mongo.save(
        this.database.getCollection("groups"),
        new Document("id", event.getGroup().getId()),
        event.getGroup());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onAuthTokenUnloaded(@NonNull AuthTokenUnloadedEvent event) {
    Mongo.save(
        this.database.getCollection("tokens"),
        new Document("token", event.getToken().getToken()),
        event.getToken());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onLinkedDataUnloaded(@NonNull LinkableUnloadedEvent event) {
    Linkable data = event.getData();
    Mongo.save(
        this.database.getCollection("links"),
        Mongo.getQuery(data.getType(), data.getIdentification()),
        data);
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onPunishmentUnloaded(@NonNull PunishmentUnloadedEvent event) {
    Punishment punishment = event.getPunishment();
    Mongo.save(
        this.database.getCollection("punishments"),
        new Document("id", punishment.getId()),
        punishment);
  }

  @Override
  public void onDisable() {
    this.client.close();
  }
}
