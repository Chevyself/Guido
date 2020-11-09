package me.googas.bot.core.handlers.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableDataType;
import me.googas.api.links.LinkableInfo;
import me.googas.api.loader.DataLoader;
import me.googas.api.matches.GlobalLadder;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.ranks.RankRange;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.adapters.LadderAdapter;
import me.googas.bot.adapters.LinkedInfoAdapter;
import me.googas.bot.adapters.LinkedValuesMapAdapter;
import me.googas.bot.adapters.LongMongoAdapter;
import me.googas.bot.adapters.PermissionAdapter;
import me.googas.bot.adapters.PermissionStackDeserializer;
import me.googas.bot.adapters.RankRangeAdapter;
import me.googas.bot.adapters.TeamAdapter;
import me.googas.bot.adapters.TeamMemberAdapter;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.api.events.data.group.GroupUnloadedEvent;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.links.LinkedDataUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.events.data.token.AuthTokenUnloadedEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.events.match.MatchUnloadedEvent;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.api.types.BotRole;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoAuthToken;
import me.googas.bot.core.types.GuidoGroup;
import me.googas.bot.core.types.GuidoGuild;
import me.googas.bot.core.types.GuidoLinkableData;
import me.googas.bot.core.types.GuidoMatch;
import me.googas.bot.core.types.GuidoRole;
import me.googas.bot.core.types.GuidoUser;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.cache.Catchable;
import me.googas.commons.cache.MemoryCache;
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
          .registerTypeAdapter(Ladder.class, new LadderAdapter())
          .registerTypeAdapter(LinkableInfo.class, new LinkedInfoAdapter())
          .registerTypeAdapter(GuidoLinkedValuesMap.class, new LinkedValuesMapAdapter())
          .registerTypeAdapter(long.class, new LongMongoAdapter())
          .registerTypeAdapter(Long.class, new LongMongoAdapter())
          .registerTypeAdapter(Permission.class, new PermissionAdapter())
          .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
          .registerTypeAdapter(RankRange.class, new RankRangeAdapter())
          .registerTypeAdapter(Team.class, new TeamAdapter())
          .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
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

  /** The collection containing matches */
  @NotNull private final MongoCollection<Document> groups;

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
    this.groups = database.getCollection("groups");
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
  public void onBotUserUnloaded(@NotNull UserUnloadedDataEvent event) {
    this.save(this.users, new Document("id", event.getData().getId()), event.getData());
  }

  /**
   * Save a group when unloaded
   *
   * @param event the event of a group being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGroupUnloadedEvent(@NotNull GroupUnloadedEvent event) {
    this.save(this.groups, new Document("id", event.getGroup().getId()), event.getGroup());
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
    BotLinkableData data = event.getData();
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
  private UserData getAllDiscordUserData(long discordId) {
    Collection<BotLinkableData> discordData = this.getDiscordData(discordId);
    for (BotLinkableData link : discordData) {
      if (link.getLinkedUser() != null) {
        return link.getLinkedUser();
      }
    }
    return new GuidoUser(this.nextUserId(), new GuidoValuesMap()).cache();
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
  @Nullable
  private <T> T getObjectFromQuery(
      @NotNull Type typeOfT,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query) {
    Document first = collection.find(query).maxAwaitTime(400, TimeUnit.MILLISECONDS).first();
    if (first != null) {
      return this.getObjectFromDocument(typeOfT, first);
    } else {
      return null;
    }
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
        T object = this.getObjectFromDocument(typeOfT, first);
        if (object instanceof BotCatchable) {
          ((BotCatchable) object).cache();
        }
        return object;
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
   * Supply all the objects matching the query. If the limit and skip are < 1 there will be no
   * elements skipped nor limited and also sort them according to the sort document
   *
   * @param typeOfT the type of objects to supply
   * @param collection the collection to find the objects from
   * @param query the query to find the documents
   * @param sort the way to sort the supplied objects
   * @param limit the limit of the documents to get
   * @param skip the amount of documents to skip
   * @param <T> the type of objects to get
   * @return the list of objects
   */
  private <T> Map<Integer, T> supplyManyFromQuerySorted(
      @NotNull Type typeOfT,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query,
      @NotNull Document sort,
      int limit,
      int skip) {
    Map<Integer, T> map = new TreeMap<>();
    MongoCursor<Document> cursor;
    if (limit != -1 && skip != -1) {
      cursor = collection.find(query).sort(sort).limit(limit).skip(skip).cursor();
    } else {
      cursor = collection.find(query).sort(sort).cursor();
    }
    int index = skip + 1;
    while (cursor.hasNext()) {
      T obj = this.getObjectFromDocument(typeOfT, cursor.next());
      if (obj != null) {
        map.put(index, obj);
        index++;
      }
    }
    return map;
  }

  /**
   * Get the objects from cache or search them in a collection
   *
   * @param clazz the class of the object to get
   * @param collection the collection to get the objects from
   * @param query the query to match the objects
   * @param limit the limit of objects to get
   * @param skip the amount of objects to skip
   * @param <T> the type of the object
   * @return the list containing the object
   */
  @NotNull
  private <T extends Catchable> List<T> supplyManyAndCache(
      @NotNull Class<T> clazz,
      @NotNull MongoCollection<Document> collection,
      @NotNull Document query,
      int limit,
      int skip,
      @NotNull Predicate<T> predicate) {
    MemoryCache cache = Guido.getCache();
    List<T> list = this.supplyManyFromQuery(clazz, collection, query, limit, skip);
    List<T> toAdd = new ArrayList<>();
    list.removeIf(
        data -> {
          if (!cache.contains(data)) {
            cache.add(data);
          } else {
            T catchable = cache.get(clazz, data::equals);
            if (catchable != null) {
              toAdd.add(catchable);
              return true;
            }
          }
          return false;
        });
    list.addAll(toAdd);
    if (limit == -1 || list.size() <= limit) {
      Collection<T> catchables = cache.getMany(clazz, predicate);
      for (T catchable : catchables) {
        if (!this.contains(list, catchable)) {
          list.add(catchable);
        }
      }
    }
    return list;
  }

  private <T extends Catchable> boolean contains(List<T> list, T catchable) {
    for (T added : list) {
      if (catchable.equals(added)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Count the amount of documents that there's with a query
   *
   * @param collection the collection to send the query
   * @param query the query to match the object
   * @return the amount of documents found with the query
   */
  public long count(@NotNull MongoCollection<Document> collection, @NotNull Document query) {
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
      @NotNull LinkableDataType type, @NotNull ValuesMap identification) {
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

  /**
   * @see DataLoader#getLinkedData(LinkableDataType, ValuesMap, boolean) this provides the linked
   *     data but with a custom predicate for different results
   * @param type the type of data to provide
   * @param identification the way to identify the data
   * @param predicate the way to get the data from cache
   * @return the data if inside of the cache else find it from query
   */
  public @Nullable BotLinkableData getLinkedData(
      @NotNull LinkableDataType type,
      @NotNull ValuesMap identification,
      @NotNull Predicate<GuidoLinkableData> predicate) {
    return Guido.getCache()
        .getOrSupply(
            GuidoLinkableData.class,
            predicate,
            this.supplyObjectFromQuery(
                GuidoLinkableData.class,
                this.links,
                this.getIdentifiableQuery(type, identification)));
  }

  /**
   * Delete an object from a collection with the given query
   *
   * @param collection the collection to delete the object from
   * @param query the way to identify the document
   * @return true if at least 1 document was deleted
   */
  public boolean deleteObject(
      @NotNull MongoCollection<Document> collection, @NotNull Document query) {
    return collection.deleteMany(query).getDeletedCount() > 0;
  }

  @Override
  public @NotNull BotLinkableData getDiscordUserData(long userId) {
    BotLinkableData data =
        this.getLinkedData(LinkableDataType.DISCORD, new GuidoValuesMap("id", userId), false);
    if (data == null) {
      data =
          new GuidoLinkableData(
                  LinkableDataType.DISCORD,
                  this.getAllDiscordUserData(userId).getId(),
                  new GuidoValuesMap("id", userId),
                  new GuidoValuesMap(),
                  new HashMap<>(),
                  new HashSet<>())
              .cache();
    }
    return data;
  }

  @Override
  public @NotNull Collection<BotLinkableData> getDiscordData(long userId) {
    return new ArrayList<>(
        this.supplyManyAndCache(
            GuidoLinkableData.class,
            this.links,
            new Document("identification.id", userId),
            -1,
            -1,
            data ->
                (data.getType() == LinkableDataType.DISCORD
                        || data.getType() == LinkableDataType.DISCORD_GUILD)
                    && data.getIdentification().getOr("id", Long.class, -1L) == userId));
  }

  @Override
  public @NotNull BotGuild getGuildDataOrCreate(long id) {
    BotGuild guild = this.getGuildData(id);
    if (guild == null) {
      return new GuidoGuild(
              id,
              new HashSet<>(),
              new HashMap<>(),
              new HashMap<>(),
              new HashMap<>(),
              new HashMap<>())
          .cache();
    }
    return guild;
  }

  @Override
  public @Nullable BotGuild getGuildData(long id) {
    return Guido.getCache()
        .getOrSupply(
            GuidoGuild.class,
            guild -> guild.getId() == id,
            this.supplyObjectFromQuery(GuidoGuild.class, this.guilds, new Document("id", id)));
  }

  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    GuidoRole guidoRole =
        Guido.getCache()
            .getOrSupply(
                GuidoRole.class,
                role -> role.getId() == id && role.getGuildId() == guildId,
                this.supplyObjectFromQuery(
                    GuidoRole.class,
                    this.roles,
                    new Document("id", id).append("guildId", guildId)));
    if (guidoRole == null) {
      return new GuidoRole(id, guildId, new HashSet<>()).cache();
    } else {
      return guidoRole;
    }
  }

  @Override
  public @Nullable UserData getUserData(@Nullable String id) {
    return Guido.getCache()
        .getOrSupply(
            GuidoUser.class,
            user -> user.getId().equals(id),
            this.supplyObjectFromQuery(GuidoUser.class, this.users, new Document("id", id)));
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    return Guido.getCache()
        .getOrSupply(
            GuidoAuthToken.class,
            guidoToken -> guidoToken.getToken().equals(token),
            this.supplyObjectFromQuery(
                GuidoAuthToken.class, this.tokens, new Document("token", token)));
  }

  @Override
  public @NotNull BotLinkableData getMemberData(long userId, long guildId) {
    GuidoValuesMap identification = new GuidoValuesMap("id", userId).put("guild", guildId);
    BotLinkableData data = this.getLinkedData(LinkableDataType.DISCORD_GUILD, identification, true);
    if (data == null) {
      data =
          new GuidoLinkableData(
                  LinkableDataType.DISCORD_GUILD,
                  this.getAllDiscordUserData(userId).getId(),
                  identification,
                  new GuidoValuesMap(),
                  new HashMap<>(),
                  new HashSet<>())
              .cache();
    }
    return data;
  }

  @Override
  public @Nullable BotLinkableData getLinkedData(
      @NotNull LinkableDataType type, @NotNull ValuesMap identification, boolean equal) {
    return this.getLinkedData(
        type,
        identification,
        data -> {
          if (data.getType() == type) {
            if (equal) {
              return data.getType() == type && data.getIdentification().equals(identification);
            } else {
              return data.getType() == type && data.getIdentification().matches(identification);
            }
          }
          return false;
        });
  }

  @Override
  public long maxPageLeaderboard(@NotNull Ladder ladder, int size) {
    return this.maxPageLeaderboard(ladder.getName() + "-elo", size);
  }

  /**
   * Get the max page of the leaderboard in a stat
   *
   * @param stat the stat to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  @Override
  public long maxPageLeaderboard(@NotNull String stat, int size) {
    return this.count(this.links, new Document("stats." + stat, new Document("$type", 1))) / size;
  }

  @Override
  public @Nullable BotMatch getMatch(@NotNull String id) {
    return Guido.getCache()
        .getOrSupply(
            GuidoMatch.class,
            match -> match.getId().equals(id),
            this.supplyObjectFromQuery(GuidoMatch.class, this.matches, new Document("id", id)));
  }

  @Override
  public @NotNull Collection<Match> getParticipating(
      @NotNull LinkableDataType type,
      @NotNull ValuesMap identification,
      @NotNull MatchStatus... status) {
    List<String> statusNames = new ArrayList<>();
    for (MatchStatus matchStatus : status) {
      statusNames.add(matchStatus.toString());
    }
    Map<String, Object> toMatch = new HashMap<>();
    toMatch.put("linkInfo.type", type.toString());
    identification
        .getMap()
        .forEach((key, value) -> toMatch.put("linkInfo.identification." + key, value));
    Document query =
        new Document("status", new Document("$in", statusNames))
            .append(
                "teams",
                new Document(
                    "$elemMatch", new Document("members", new Document("$elemMatch", toMatch))));
    return new ArrayList<>(
        this.supplyManyAndCache(
            GuidoMatch.class,
            this.matches,
            query,
            -1,
            -1,
            match -> match.isParticipating(type, identification)));
  }

  @Override
  public @Nullable BotGroup getGroup(@NotNull String id) {
    return Guido.getCache()
        .getOrSupply(
            GuidoGroup.class,
            group -> group.getId().equals(id),
            this.supplyObjectFromQuery(GuidoGroup.class, this.groups, new Document("id", id)));
  }

  /**
   * Delete the group with the given id
   *
   * @param id the id of the group to delete
   * @return true if the group was deleted
   */
  @Override
  public boolean deleteGroup(@NotNull String id) {
    BotGroup group = this.getGroup(id);
    if (group != null) {
      try {
        group.unload(false);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return this.deleteObject(this.groups, new Document("id", id));
    }
    return false;
  }

  @Override
  public @NotNull Collection<Group> getGroups() {
    ArrayList<Group> groups =
        new ArrayList<>(
            this.supplyManyAndCache(
                GuidoGroup.class, this.groups, new Document(), -1, -1, group -> true));
    groups.sort(Comparator.comparingInt(Group::getWeight));
    return groups;
  }

  @Override
  public @NotNull Map<Integer, LinkableData> getLeaderboard(
      @NotNull Ladder ladder, int page, int size) {
    if (!(ladder instanceof GlobalLadder)) {
      return this.getLeaderboard(ladder.getName() + "-elo", page, size, false);
    }
    throw new IllegalArgumentException("Leaderboard is not available for global ladder");
  }

  @Override
  public @NotNull Map<Integer, LinkableData> getLeaderboard(
      @NotNull String stat, int page, int size, boolean inverted) {
    return this.supplyManyFromQuerySorted(
        GuidoLinkableData.class,
        this.links,
        new Document("stats." + stat, new Document("$type", 1)),
        new Document("stats." + stat, inverted ? 1 : -1),
        size,
        page * size);
  }

  /**
   * Get all the matches
   *
   * @param page the page of matches to see
   * @param size the size of the page
   * @param statuses the status of the matches to get
   * @return the collection of matches
   */
  @Override
  public @NotNull Collection<Match> getMatches(
      int page, int size, @NotNull MatchStatus... statuses) {
    Set<MatchStatus> toMatch = new HashSet<>();
    Set<String> names = new HashSet<>();
    if (statuses.length == 0) {
      toMatch.addAll(Arrays.asList(MatchStatus.values()));
    } else {
      toMatch.addAll(Arrays.asList(statuses));
    }
    for (MatchStatus match : toMatch) {
      names.add(match.toString());
    }
    return new ArrayList<>(
        this.supplyManyAndCache(
            GuidoMatch.class,
            this.matches,
            new Document("type", new Document("$in", names)),
            size,
            page * size,
            match -> true));
  }

  @Override
  public @NotNull Collection<LinkableData> getLinks(@NotNull UserData user) {
    return this.getLinks(user, LinkableDataType.values());
  }

  @Override
  public @NotNull Collection<LinkableData> getLinks(
      @NotNull UserData user, @NotNull LinkableDataType... types) {
    List<String> typesName = new ArrayList<>();
    for (LinkableDataType type : types) {
      typesName.add(type.toString());
    }
    Set<LinkableDataType> typeSet = new HashSet<>(Arrays.asList(types));
    return new ArrayList<>(
        this.supplyManyAndCache(
            GuidoLinkableData.class,
            this.links,
            new Document("linked-id", user.getId()).append("type", new Document("$in", typesName)),
            -1,
            -1,
            data ->
                user.getId().equals(data.getLinkedUserId()) && typeSet.contains(data.getType())));
  }

  @NotNull
  @Override
  public Collection<AuthToken> getTokens(@NotNull UserData user) {
    return this.supplyManyFromQuery(
        GuidoAuthToken.class, this.tokens, new Document("user", user.getId()), -1, -1);
  }
}
