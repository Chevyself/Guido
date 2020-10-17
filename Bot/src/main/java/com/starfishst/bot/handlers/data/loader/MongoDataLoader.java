package com.starfishst.bot.handlers.data.loader;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.bot.api.events.data.links.LinkedDataUnloadedEvent;
import com.starfishst.bot.api.events.data.role.BotRoleUnloadedEvent;
import com.starfishst.bot.api.events.data.token.AuthTokenUnloadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.bot.handlers.data.GuidoAuthToken;
import com.starfishst.bot.handlers.data.GuidoGuild;
import com.starfishst.bot.handlers.data.GuidoLinkedData;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoPermissionStack;
import com.starfishst.bot.handlers.data.GuidoRankRange;
import com.starfishst.bot.handlers.data.GuidoRole;
import com.starfishst.bot.handlers.data.GuidoUser;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.bot.util.console.Console;
import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.data.token.AuthToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import me.googas.commons.Lots;
import me.googas.commons.cache.Cache;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads the data from a mongo database */
public class MongoDataLoader implements BotDataLoader {

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

  /**
   * Create the mongo data loader
   *
   * @param uri the mongo uri to connect
   * @param databaseName the name of the database
   */
  public MongoDataLoader(@NotNull String uri, @NotNull String databaseName) {
    this.client = MongoClients.create(uri);
    MongoDatabase database = this.client.getDatabase(databaseName);
    this.guilds = database.getCollection("guilds");
    this.roles = database.getCollection("roles");
    this.users = database.getCollection("users");
    this.links = database.getCollection("links");
    this.tokens = database.getCollection("tokens");
  }

  /**
   * This will listen to when the guild data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NotNull BotGuildUnloadedEvent event) {
    BotGuild data = event.getData();
    Document query = new Document("id", data.getId());
    // TODO append the document
    Document document =
        new Document("id", data.getId())
            .append("multipliers", data.getMultipliers())
            .append("ladders", data.getLadders());
    Document first = this.guilds.find(query).first();
    if (first != null) {
      this.guilds.replaceOne(query, document);
    } else {
      this.guilds.insertOne(document);
    }
  }

  /**
   * Listener to linked data being unloaded to save it in cache
   *
   * @param event the event of linked data being unloaded
   */
  @Listener
  public void onLinkedDataUnloadedEvent(@NotNull LinkedDataUnloadedEvent event) {
    BotLinkedData data = event.getData();
    Document document = new Document("type", data.getType().toString());
    if (data.getLinkedUser() != null) {
      document.append("linked-id", data.getLinkedUser().getId());
    }
    document.append("identification", data.getIdentification().getMap());
    document.append("preferences", data.getPreferences().getMap());
    document.append("stats", data.getStats());
    document.append("permissions", this.getPermissionStacksDocument(data));
    Document query =
        new Document("type", data.getType().toString())
            .append("identification", data.getIdentification().getMap());
    Document first = this.links.find(query).first();
    if (first != null) {
      this.links.replaceOne(query, document);
    } else {
      this.links.insertOne(document);
    }
  }

  /**
   * Listen to an auth token being unloaded to save it
   *
   * @param event the event of an auth token being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onAuthTokenUnloaded(@NotNull AuthTokenUnloadedEvent event) {
    AuthToken token = event.getToken();
    Document document =
        new Document("token", token.getToken())
            .append("level", token.getLevel().toString())
            .append("user", token.getUser().getId());
    Document query = new Document("token", token.getToken());
    Document first = this.tokens.find(query).first();
    if (first != null) {
      this.tokens.replaceOne(query, document);
    } else {
      this.tokens.insertOne(document);
    }
  }

  /**
   * Get the document of a permission stack from a permissible
   *
   * @param permissible the permissible to get the stacks from
   * @return the document of the permission stack
   */
  @NotNull
  private List<Document> getPermissionStacksDocument(@NotNull Permissible<?> permissible) {
    List<Document> stack = new ArrayList<>();
    for (PermissionStack<?> permission : permissible.getPermissions()) {
      List<String> nodes = new ArrayList<>();
      for (Permission permissionPermission : permission.getPermissions()) {
        nodes.add(permissionPermission.getNodeAppended());
      }
      stack.add(new Document("context", permission.getContext()).append("permissions", nodes));
    }
    return stack;
  }

  /**
   * This will listen to when the role data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleDataUnloaded(@NotNull BotRoleUnloadedEvent event) {
    Document query =
        new Document("id", event.getData().getId()).append("guildId", event.getData().getGuildId());
    Document first = this.roles.find(query).first();
    Document document =
        new Document("id", event.getData().getId()).append("guildId", event.getData().getGuildId());
    document.put("permissions", this.getPermissionStacksDocument(event.getData()));
    if (first != null) {
      this.roles.replaceOne(query, document);
    } else {
      this.roles.insertOne(document);
    }
  }

  /**
   * This will listen to when the user data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onUserDataUnloaded(@NotNull BotUserUnloadedEvent event) {
    Document query = new Document("id", event.getData().getId());
    Document first = this.users.find(query).first();
    Document document = new Document("id", event.getData().getId());
    if (first != null) {
      this.users.replaceOne(query, document);
    } else {
      this.users.insertOne(document);
    }
  }

  /**
   * Get the stats of a member from a document
   *
   * @param document the document to get the stats from
   * @return the stats from the document
   */
  @NotNull
  private HashMap<String, Double> getStats(@NotNull Document document) {
    return this.getMap(document, "stats", Double.TYPE);
  }

  /**
   * Get a map from a document given the key and the values that it accepts
   *
   * @param document the document to get the map from
   * @param key the key which contains the map inside the document
   * @param vClass the class of values that it accepts
   * @param <V> the type of the value
   * @return the map with the values found in the document if none are found the map is empty
   */
  @NotNull
  private <V> HashMap<String, V> getMap(
      @NotNull Document document, @NotNull String key, @NotNull Class<V> vClass) {
    HashMap<String, V> map = new HashMap<>();
    if (document.get(key) instanceof Document) {
      document
          .get(key, Document.class)
          .forEach(
              (docKey, value) -> {
                if (vClass.isAssignableFrom(value.getClass())) {
                  map.put(docKey, vClass.cast(value));
                }
              });
    }
    return map;
  }

  /**
   * Get the permission stack from a document
   *
   * @param document the document to get the stack from
   * @return the set of the permission stacks
   */
  @NotNull
  private Set<GuidoPermissionStack> getPermissionStacks(@NotNull Document document) {
    Set<GuidoPermissionStack> permissions = new HashSet<>();
    if (document.get("permissions") instanceof List) {
      for (Document stackDocument : document.getList("permissions", Document.class)) {
        Set<GuidoPermission> guidoPermissions = new HashSet<>();
        List<String> stack = stackDocument.getList("permissions", String.class);
        for (String string : stack) {
          if (string.startsWith("-")) {
            guidoPermissions.add(new GuidoPermission(string.substring(1), false));
          } else {
            guidoPermissions.add(new GuidoPermission(string, true));
          }
        }
        permissions.add(
            new GuidoPermissionStack(stackDocument.getString("context"), guidoPermissions));
      }
    }
    return permissions;
  }

  @Override
  public void close() {
    this.client.close();
  }

  /**
   * Get the data of a guild using a query
   *
   * @param query the query to get the guild data
   * @return the guild data
   */
  public @Nullable GuidoGuild getGuildData(@NotNull Document query) {
    Document document = this.guilds.find(query).first();
    if (document != null) {
      return new GuidoGuild(
          document.getLong("id"),
          this.getMultipliers(document),
          this.getLadders(document),
          this.getRange(document));
    }
    return null;
  }

  /**
   * Get the map of multipliers from a document
   *
   * @param document the document to get the map from
   * @return the map
   */
  private HashMap<String, Integer> getMultipliers(Document document) {
    return this.getMap(document, "multipliers", Integer.class);
  }

  /**
   * Get the map of ladders from a document
   *
   * @param document the document to get the map from
   * @return the map
   */
  private HashMap<String, Integer> getLadders(Document document) {
    return this.getMap(document, "ladders", Integer.class);
  }

  /**
   * Get the map of range from a document
   *
   * @param document the document to get the map from
   * @return the map
   */
  @NotNull
  private HashMap<Long, GuidoRankRange> getRange(Document document) {
    HashMap<String, Document> laddersDocs = this.getMap(document, "ladders", Document.class);
    HashMap<Long, GuidoRankRange> ladders = new HashMap<>();
    laddersDocs.forEach(
        (key, doc) -> {
          try {
            ladders.put(
                Long.parseLong(key),
                new GuidoRankRange(
                    doc.getString("ladder"), doc.getInteger("min"), doc.getInteger("max")));
          } catch (NumberFormatException e) {
            Console.exception(e, "Error while parsing long");
          }
        });
    return ladders;
  }

  /**
   * Get the data of a role using a query
   *
   * @param query the query to get the role data
   * @return the role data
   */
  public @Nullable GuidoRole getRoleData(@NotNull Document query) {
    Document document = this.roles.find(query).first();
    if (document != null) {
      return new GuidoRole(
          document.getLong("id"), document.getLong("guildId"), this.getPermissionStacks(document));
    }
    return null;
  }

  /**
   * Get the data of an user using a query
   *
   * @param query the query to get the user data
   * @return the user data
   */
  public @Nullable GuidoUser getUserData(@NotNull Document query) {
    Document document = this.users.find(query).first();
    if (document != null) {
      return new GuidoUser(document.getString("id"));
    }
    return null;
  }

  /**
   * Get the values map from a document
   *
   * @param document the document to get the values from
   * @return the values
   */
  @NotNull
  private GuidoValuesMap getPreferences(@NotNull Document document) {
    GuidoValuesMap map = new GuidoValuesMap();
    if (document.get("preferences") != null) {
      map.addValues(document.get("preferences", Document.class));
    }
    return map;
  }

  /**
   * Get an auth token using a query
   *
   * @param query the query to get the token
   * @param addToCache whether to add the token to cache
   * @return the token if found in the database else null
   */
  public @Nullable GuidoAuthToken getAuthToken(@NotNull Document query, boolean addToCache) {
    Document document = this.tokens.find(query).first();
    if (document != null) {
      return this.getGuidoAuthToken(document, addToCache);
    }
    return null;
  }

  /**
   * Get a guido auth token from cache
   *
   * @param document the document to get the token from
   * @param addToCache whether to add the token to cache
   * @return the token. This can return null if the user of the token is not found
   */
  @Nullable
  private GuidoAuthToken getGuidoAuthToken(@NotNull Document document, boolean addToCache) {
    BotUser user = this.getUserData(document.getString("user"));
    if (user != null) {
      return new GuidoAuthToken(
          document.getString("token"),
          AuthLevel.valueOf(document.getString("level")),
          user,
          addToCache);
    } else {
      return null;
    }
  }

  /**
   * Get a bot user providing a discordId. This will try to match a member data or user data
   *
   * <p>All discord links need of a bot user, this ensures that it does not miss it
   *
   * @param discordId the discord id to match
   * @return the user if found one else null
   */
  @NotNull
  private BotUser getUserData(long discordId) {
    List<BotLinkedData> data = this.getDiscordData(discordId);
    BotUser user = null;
    for (BotLinkedData link : data) {
      if (link.getLinkedUser() != null) {
        user = link.getLinkedUser();
        break;
      }
    }
    if (user == null) {
      user = new GuidoUser(this.nextUserId());
    }
    return user;
  }

  /**
   * Get linked data from a query
   *
   * @param query the query to get the data from
   * @return the linked data if found else null
   */
  @Nullable
  private GuidoLinkedData getLinkedData(@NotNull Document query) {
    Document first = this.links.find(query).first();
    if (first != null) {
      return this.getGuidoLinkedData(first, true);
    }
    return null;
  }

  /**
   * Get all the links from a query. The elements in this list are not in cache
   *
   * @param query the query to get the links from
   * @return the links from the query
   */
  public @NotNull List<BotLinkedData> getLinks(@NotNull Document query) {
    List<BotLinkedData> links = new ArrayList<>();
    MongoCursor<Document> cursor = this.links.find(query).cursor();
    while (cursor.hasNext()) {
      links.add(this.getGuidoLinkedData(cursor.next(), false));
    }
    return links;
  }

  /**
   * Get guido linked data from a document
   *
   * @param document the document to get the linked data from
   * @param addToCache whether to add the data to cache
   * @return the guido linked data
   */
  @NotNull
  private GuidoLinkedData getGuidoLinkedData(@NotNull Document document, boolean addToCache) {
    BotUser user = null;
    if (document.get("linked-id") != null) {
      user = this.getUserData(document.getString("linked-id"));
    }
    return new GuidoLinkedData(
        addToCache,
        LinkedDataType.valueOf(document.getString("type")),
        user,
        this.getIdentificationMap(document),
        this.getPreferences(document),
        this.getStats(document),
        this.getPermissionStacks(document));
  }

  /**
   * Get the identification map from a document
   *
   * @param document the document to get the identification map from
   * @return the identification map
   */
  @NotNull
  private GuidoValuesMap getIdentificationMap(@NotNull Document document) {
    GuidoValuesMap identification = new GuidoValuesMap();
    if (document.get("identification") != null) {
      identification.addValues(document.get("identification", Document.class));
    }
    return identification;
  }

  @Override
  public @NotNull BotGuild getGuildData(long id) {
    return Cache.getCatchableOrGet(
        GuidoGuild.class,
        guild -> guild.getId() == id,
        () -> {
          GuidoGuild guild = this.getGuildData(new Document("id", id));
          if (guild == null) {
            guild = new GuidoGuild(id, new HashMap<>(), new HashMap<>(), new HashMap<>());
          }
          return guild;
        });
  }

  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    return Cache.getCatchableOrGet(
        GuidoRole.class,
        role -> role.getId() == id && role.getGuildId() == guildId,
        () -> {
          GuidoRole role = this.getRoleData(new Document("id", id).append("guildId", guildId));
          if (role == null) {
            role = new GuidoRole(id, guildId, new HashSet<>());
          }
          return role;
        });
  }

  @Override
  public @Nullable BotUser getUserData(@NotNull String id) {
    return Cache.getCatchableOrGet(
        GuidoUser.class,
        user -> user.getId().equals(id),
        () -> this.getUserData(new Document("id", id)));
  }

  /**
   * Get linked data using it's type and identifications
   *
   * @param type the type of data to find
   * @param identifications the way to identify the data
   * @return the linked data if found else null
   */
  @Override
  public @Nullable BotLinkedData getLinkedData(
      @NotNull LinkedDataType type, @NotNull ValuesMap identifications) {
    return Cache.getCatchableOrGet(
        GuidoLinkedData.class,
        data -> data.getType() == type && data.getIdentification().matches(identifications),
        () -> {
          Document query = new Document("type", type.toString());
          identifications
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
          return this.getLinkedData(query);
        });
  }

  @Override
  public @Nullable Match getMatch(@NotNull String id) {
    // TODO implement this
    throw new UnsupportedOperationException("Matches have not been implemented yet");
  }

  /**
   * Get the discord data for an user
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @Override
  public @NotNull BotLinkedData getDiscordUserData(long userId) {
    return Cache.getCatchableOrGet(
        BotLinkedData.class,
        data ->
            data.getType() == LinkedDataType.DISCORD
                && data.getIdentification().getValueOr("id", Long.class, -1L) == userId,
        () -> {
          BotLinkedData data =
              this.getLinkedData(LinkedDataType.DISCORD, new GuidoValuesMap("id", userId));
          if (data == null) {
            data =
                new GuidoLinkedData(
                    true,
                    LinkedDataType.DISCORD,
                    this.getUserData(userId),
                    new GuidoValuesMap(Maps.singleton("id", userId)),
                    new GuidoValuesMap(),
                    new HashMap<>(),
                    new HashSet<>());
          }
          return data;
        });
  }

  /**
   * Get the discord data for a member
   *
   * @param userId the id of the user to get the data
   * @param guildId the id of the guild where the user is member from
   * @return the data of the user
   */
  @Override
  public @NotNull BotLinkedData getMemberData(long userId, long guildId) {
    return Cache.getCatchableOrGet(
        BotLinkedData.class,
        data ->
            data.getType() == LinkedDataType.DISCORD_GUILD
                && data.getIdentification().getValueOr("id", Long.class, -1L) == userId
                && data.getIdentification().getValueOr("guild", Long.class, -1L) == guildId,
        () -> {
          BotLinkedData data =
              this.getLinkedData(
                  LinkedDataType.DISCORD_GUILD,
                  new GuidoValuesMap("id", userId).addValue("guild", guildId));
          if (data == null) {
            data =
                new GuidoLinkedData(
                    true,
                    LinkedDataType.DISCORD,
                    this.getUserData(userId),
                    new GuidoValuesMap(Maps.singleton("id", userId)),
                    new GuidoValuesMap(),
                    new HashMap<>(),
                    new HashSet<>());
          }
          return data;
        });
  }

  /**
   * Get all the discord data matching a discord user id
   *
   * @param userId the id of the user to get the data
   * @return the data of the user
   */
  @Override
  public @NotNull List<BotLinkedData> getDiscordData(long userId) {
    List<BotLinkedData> data =
        new ArrayList<>(
            Cache.getCatchables(
                GuidoLinkedData.class,
                linked ->
                    (linked.getType() == LinkedDataType.DISCORD
                            || linked.getType() == LinkedDataType.DISCORD_GUILD)
                        && linked.getIdentification().getValueOr("id", Long.class, -1L) == userId));
    List<BotLinkedData> links = this.getLinks(new Document("id", userId));
    Lots.addIf(
        data,
        links,
        linked -> {
          if (!data.contains(linked)) {
            linked.addToCache();
            return true;
          }
          return false;
        });
    return data;
  }

  /**
   * Get the links from an user
   *
   * @param user the user to get the links from
   * @return the links
   */
  @Override
  public @NotNull Collection<BotLinkedData> getLinks(@NotNull UserData user) {
    List<BotLinkedData> data =
        new ArrayList<>(
            Cache.getCatchables(
                GuidoLinkedData.class, linked -> user.equals(linked.getLinkedUser())));
    List<BotLinkedData> links = this.getLinks(new Document("linked-id", user.getId()));
    Lots.addIf(
        data,
        links,
        linked -> {
          if (!data.contains(linked)) {
            linked.addToCache();
            return true;
          }
          return false;
        });
    return data;
  }

  @Override
  public Collection<GuidoAuthToken> getTokens(@NotNull UserData user) {
    List<GuidoAuthToken> catchables =
        Cache.getCatchables(GuidoAuthToken.class, token -> token.getUser().equals(user));
    MongoCursor<Document> cursor = this.tokens.find(new Document("user", user.getId())).cursor();
    while (cursor.hasNext()) {
      GuidoAuthToken token = this.getGuidoAuthToken(cursor.next(), false);
      if (token != null && !catchables.contains(token)) {
        token.addToCache();
        catchables.add(token);
      }
    }
    return catchables;
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    return Cache.getCatchableOrGet(
        AuthToken.class,
        cacheToken -> cacheToken.getToken().equalsIgnoreCase(token),
        () -> this.getAuthToken(new Document("token", token), true));
  }
}
