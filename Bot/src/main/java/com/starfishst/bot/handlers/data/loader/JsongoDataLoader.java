package com.starfishst.bot.handlers.data.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.starfishst.bot.adapters.LongMongoAdapter;
import com.starfishst.bot.adapters.PermissionAdapter;
import com.starfishst.bot.adapters.ValuesMapAdapter;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.bot.api.events.data.links.LinkedDataUnloadedEvent;
import com.starfishst.bot.api.events.data.match.MatchUnloadedEvent;
import com.starfishst.bot.api.events.data.role.BotRoleUnloadedEvent;
import com.starfishst.bot.api.events.data.token.AuthTokenUnloadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.bot.handlers.data.GuidoAuthToken;
import com.starfishst.bot.handlers.data.GuidoGuild;
import com.starfishst.bot.handlers.data.GuidoLinkedData;
import com.starfishst.bot.handlers.data.GuidoMatch;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoRole;
import com.starfishst.bot.handlers.data.GuidoUser;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.token.AuthToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A data loader that uses both mongo and json
 *
 * <h1>IMPORTANT Mongo does not support dots '.' in their field names!!</h1>
 */
public class JsongoDataLoader implements BotDataLoader {

  /** The gson instance for deserialization */
  @NotNull
  private final Gson gson =
      new GsonBuilder()
          .setPrettyPrinting()
          .registerTypeAdapter(long.class, new LongMongoAdapter())
          .registerTypeAdapter(Long.class, new LongMongoAdapter())
          .registerTypeAdapter(Permission.class, new PermissionAdapter())
          .registerTypeAdapter(GuidoPermission.class, new PermissionAdapter())
          .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
          .registerTypeAdapter(GuidoValuesMap.class, new ValuesMapAdapter())
          .create();

  /** The mongo client to have access to collections */
  @NotNull private final MongoClient client;

  /** The collection containing guild data */
  @NotNull private final MongoCollection<Document> guilds;

  /** The collection containing roles data */
  @NotNull private final MongoCollection<Document> roles;

  /** The collection containing users data */
  @NotNull private final MongoCollection<Document> users;

  /** The links of users */
  @NotNull private final MongoCollection<Document> links;

  /** The collection containing tokens data */
  @NotNull private final MongoCollection<Document> tokens;

  /** The collection containing matches */
  @NotNull private final MongoCollection<Document> matches;

  /**
   * Create the mongo data loader
   *
   * @param uri the mongo uri to connect
   * @param databaseName the name of the database
   */
  public JsongoDataLoader(@NotNull String uri, @NotNull String databaseName) {
    this.client = MongoClients.create(uri);
    MongoDatabase database = this.client.getDatabase(databaseName);
    this.guilds = database.getCollection("guilds");
    this.roles = database.getCollection("roles");
    this.users = database.getCollection("users");
    this.links = database.getCollection("links");
    this.tokens = database.getCollection("tokens");
    this.matches = database.getCollection("matches");
  }

  /**
   * Save guild data when unloaded
   *
   * @param event the event of a guild being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotGuildUnloaded(@NotNull BotGuildUnloadedEvent event) {
    this.save(this.guilds, new Document("id", event.getData().getId()), event.getData());
  }

  /**
   * Save a match when unloaded
   *
   * @param event the event of a match being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchUnloaded(@NotNull MatchUnloadedEvent event) {
    this.save(this.matches, new Document("id", event.getMatch().getId()), event.getMatch());
  }

  /**
   * Save the data of a role when unloaded
   *
   * @param event the event of a role being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotRoleUnloaded(@NotNull BotRoleUnloadedEvent event) {
    this.save(this.roles, new Document("id", event.getData().getId()), event.getData());
  }

  /**
   * Save the data of an user when unloaded
   *
   * @param event the event of an user being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotUserUnloaded(@NotNull BotUserUnloadedEvent event) {
    this.save(this.users, new Document("id", event.getData().getId()), event.getData());
  }

  /**
   * Save the data of a token being unloaded
   *
   * @param event the event of a token being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onAuthTokenUnloaded(@NotNull AuthTokenUnloadedEvent event) {
    this.save(this.tokens, new Document("token", event.getToken().getToken()), event.getToken());
  }

  /**
   * Save the linked data when unloaded
   *
   * @param event the event of linked data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onLinkedDataUnloaded(@NotNull LinkedDataUnloadedEvent event) {
    BotLinkedData data = event.getData();
    Document query =
        new Document("type", data.getType().toString())
            .append("identification", data.getIdentification().getMap());
    this.save(this.links, query, event.getData());
  }

  /**
   * Saves an object into the mongo collection
   *
   * @param collection the mongo collection to save the document to
   * @param query the query to replace in case that the document already exists
   * @param object the object to save
   */
  private void save(
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query,
      @NotNull Object object) {
    Document document = Document.parse(this.gson.toJson(object));
    Document first = collection.find(query).first();
    if (first != null) {
      collection.replaceOne(query, document);
    } else {
      collection.insertOne(document);
    }
  }

  /**
   * Get an user using a discord id
   *
   * @param discordId the discord if of the user
   * @return the user. If the user is null we will create one
   */
  @NotNull
  private BotUser getUserData(long discordId) {
    Collection<BotLinkedData> discordData = this.getDiscordData(discordId);
    for (BotLinkedData link : discordData) {
      if (link.getLinkedUser() != null) {
        return link.getLinkedUser();
      }
    }
    return new GuidoUser(this.nextUserId());
  }

  /**
   * Get an object from a query. This works for getting one object only
   *
   * @param typeOfT the type of object to supply
   * @param collection the collection to get the object from
   * @param query the query to match the object
   * @param <T> the type of the object
   * @return the supplier of the object
   */
  @NotNull
  private <T> Supplier<T> supplyObjectFromQuery(
      @NotNull Type typeOfT,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query) {
    return () -> {
      Document first = collection.find(query).maxAwaitTime(400, TimeUnit.MILLISECONDS).first();
      if (first != null) {
        return this.getObjectFromDocument(typeOfT, first);
      } else {
        return null;
      }
    };
  }

  /**
   * Supply all the objects matching the query. If the limit and skip are < 1 there will be no
   * elements skipped nor limited
   *
   * @param typeOfT the type of objects to supply
   * @param collection the collection to find the objects from
   * @param query the query to find the documents
   * @param limit the limit of the documents to get
   * @param skip the amount of documents to skip
   * @param <T> the type of objects to get
   * @return the list of objects
   */
  private <T> List<T> supplyManyFromQuery(
      @NotNull Type typeOfT,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query,
      int limit,
      int skip) {
    List<T> list = new ArrayList<>();
    MongoCursor<Document> cursor;
    if (limit != -1 && skip != -1) {
      cursor = collection.find(query).limit(limit).skip(skip).cursor();
    } else {
      cursor = collection.find(query).cursor();
    }
    while (cursor.hasNext()) {
      T obj = this.getObjectFromDocument(typeOfT, cursor.next());
      if (obj != null) {
        list.add(obj);
      }
    }
    return list;
  }

  /**
   * Get the objects from cache or search them in a collection
   *
   * @param clazz the class of the object to get
   * @param predicate the predicate to find the objects in cache
   * @param collection the collection to get the objects from
   * @param query the query to match the objects
   * @param limit the limit of objects to get
   * @param skip the amount of objects to skip
   * @param <T> the type of the object
   * @return the list containing the object
   */
  @NotNull
  private <T extends ICatchable> List<T> supplyManyAndCache(
      @NotNull Class<T> clazz,
      @NotNull Predicate<T> predicate,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query,
      int limit,
      int skip) {
    List<T> cached = Cache.getCatchables(clazz, predicate);
    List<T> data = this.supplyManyFromQuery(clazz, collection, query, limit, skip);
    for (T loaded : data) {
      if (!cached.contains(loaded)) {
        loaded.addToCache();
        loaded.refresh();
        cached.add(loaded);
      }
    }
    return new ArrayList<>(cached);
  }

  /**
   * Count the amount of documents that there's with a query
   *
   * @param collection the collection to send the query
   * @param query the query to match the object
   * @return the amount of documents found with the query
   */
  private long count(@NotNull MongoCollection<Document> collection, @NotNull Document query) {
    return collection.countDocuments(query);
  }

  /**
   * Get an object given a document
   *
   * @param typeOfT the type of object to get from document
   * @param document the document to get the object from
   * @param <T> the type of the object
   * @return the object given by json
   */
  @Nullable
  private <T> T getObjectFromDocument(@NotNull Type typeOfT, @NotNull Document document) {
    return this.gson.fromJson(document.toJson(), typeOfT);
  }

  /**
   * Create the query for an object with identification
   *
   * @param type the type of linked data to match
   * @param identification the way to identify the data
   * @return the query as a document
   */
  @NotNull
  private Document getIdentifiableQuery(
      @NotNull LinkedDataType type, @NotNull ValuesMap identification) {
    Document query = new Document("type", type.toString());
    identification
        .getMap()
        .forEach(
            (key, value) -> {
              if (key.startsWith("nick") && value instanceof String) {
                query.append(
                    "identification." + key,
                    Pattern.compile((String) value, Pattern.CASE_INSENSITIVE));
              } else {
                query.append("identification." + key, value);
              }
            });
    return query;
  }

  @Override
  public void close() {
    this.client.close();
  }

  @Override
  public @NotNull BotLinkedData getDiscordUserData(long userId) {
    BotLinkedData data =
        this.getLinkedData(LinkedDataType.DISCORD, new GuidoValuesMap("id", userId));
    if (data == null) {
      data =
          new GuidoLinkedData(
              true,
              LinkedDataType.DISCORD,
              this.getUserData(userId).getId(),
              new GuidoValuesMap("id", userId),
              new GuidoValuesMap(),
              new HashMap<>(),
              new HashSet<>());
    }
    return data;
  }

  @Override
  public @NotNull BotLinkedData getMemberData(long userId, long guildId) {
    BotLinkedData data =
        this.getLinkedData(
            LinkedDataType.DISCORD_GUILD,
            new GuidoValuesMap("id", userId).addValue("guildId", guildId));
    if (data == null) {
      data =
          new GuidoLinkedData(
              true,
              LinkedDataType.DISCORD_GUILD,
              this.getUserData(userId).getId(),
              new GuidoValuesMap("id", userId),
              new GuidoValuesMap(),
              new HashMap<>(),
              new HashSet<>());
    }
    return data;
  }

  @Override
  public @NotNull Collection<BotLinkedData> getDiscordData(long userId) {
    return new ArrayList<>(
        this.supplyManyAndCache(
            GuidoLinkedData.class,
            link ->
                (link.getType() == LinkedDataType.DISCORD
                        || link.getType() == LinkedDataType.DISCORD_GUILD)
                    && userId == link.getIdentification().getValueOr("id", Long.class, -1L),
            this.links,
            new Document("identification.id", userId),
            -1,
            -1));
  }

  @Override
  public @NotNull BotGuild getGuildData(long id) {
    GuidoGuild guidoGuild =
        Cache.getCatchableOrGet(
            GuidoGuild.class,
            guild -> guild.getId() == id,
            this.supplyObjectFromQuery(GuidoGuild.class, this.guilds, new Document("id", id)));
    if (guidoGuild == null) {
      guidoGuild = new GuidoGuild(id, new HashMap<>(), new HashSet<>(), new HashMap<>());
    }
    return guidoGuild;
  }

  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    return Cache.getCatchableOrGet(
        GuidoRole.class,
        role -> role.getId() == id && role.getGuildId() == guildId,
        this.supplyObjectFromQuery(
            GuidoRole.class, this.roles, new Document("id", id).append("guildId", guildId)));
  }

  @Override
  public @Nullable BotUser getUserData(@Nullable String id) {
    return Cache.getCatchableOrGet(
        GuidoUser.class,
        user -> user.getId().equals(id),
        this.supplyObjectFromQuery(GuidoUser.class, this.users, new Document("id", id)));
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    return Cache.getCatchableOrGet(
        GuidoAuthToken.class,
        guidoToken -> guidoToken.getToken().equals(token),
        this.supplyObjectFromQuery(
            GuidoAuthToken.class, this.tokens, new Document("token", token)));
  }

  @Override
  public @Nullable BotLinkedData getLinkedData(
      @NotNull LinkedDataType type, @NotNull ValuesMap identification) {
    return Cache.getCatchableOrGet(
        GuidoLinkedData.class,
        data -> data.getType() == type && data.getIdentification().matches(identification),
        this.supplyObjectFromQuery(
            GuidoLinkedData.class, this.links, this.getIdentifiableQuery(type, identification)));
  }

  @Override
  public @Nullable Match getMatch(@NotNull String id) {
    return Cache.getCatchableOrGet(
        GuidoMatch.class,
        match -> match.getId().equals(id),
        this.supplyObjectFromQuery(GuidoMatch.class, this.matches, new Document("id", id)));
  }

  @Override
  public @NotNull Collection<BotLinkedData> getLinks(@NotNull UserData user) {
    return new ArrayList<>(
        this.supplyManyAndCache(
            GuidoLinkedData.class,
            link -> user.getId().equals(link.getLinkedUserId()),
            this.links,
            new Document("linked-user", user.getId()),
            -1,
            -1));
  }

  @NotNull
  @Override
  public Collection<? extends AuthToken> getTokens(@NotNull UserData user) {
    return this.supplyManyFromQuery(
        GuidoAuthToken.class, this.tokens, new Document("user", user.getId()), -1, -1);
  }
}
