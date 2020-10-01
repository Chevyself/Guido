package com.starfishst.bot.handlers.data.loader;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUnlinkedMember;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.bot.api.events.data.member.BotMemberUnloadedEvent;
import com.starfishst.bot.api.events.data.role.BotRoleUnloadedEvent;
import com.starfishst.bot.api.events.data.token.AuthTokenUnloadedEvent;
import com.starfishst.bot.api.events.data.unlinked.BotUnlinkedMemberUnloadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.bot.handlers.data.GuidoAuthToken;
import com.starfishst.bot.handlers.data.GuidoGuild;
import com.starfishst.bot.handlers.data.GuidoMember;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoPermissionStack;
import com.starfishst.bot.handlers.data.GuidoRole;
import com.starfishst.bot.handlers.data.GuidoUnlinkedMember;
import com.starfishst.bot.handlers.data.GuidoUser;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.data.AuthToken;
import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.UnlinkedMember;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads the data from a mongo database */
public class MongoDataLoader implements BotDataLoader {

  /** The mongo client to have access to collections */
  @NotNull private final MongoClient client;

  /** The collection containing guild data */
  @NotNull private final MongoCollection<Document> guilds;

  /** The collection containing members data */
  @NotNull private final MongoCollection<Document> members;

  /** The collection containing roles data */
  @NotNull private final MongoCollection<Document> roles;

  /** The collection containing users data */
  @NotNull private final MongoCollection<Document> users;

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
    this.members = database.getCollection("guilds");
    this.roles = database.getCollection("roles");
    this.users = database.getCollection("users");
    this.tokens = database.getCollection("tokens");
  }

  /**
   * This will listen to when the guild data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NotNull BotGuildUnloadedEvent event) {
    Document query = new Document("id", event.getData().getId());
    Document document = new Document("id", event.getData().getId());
    Document first = this.guilds.find(query).first();
    if (first != null) {
      this.guilds.replaceOne(query, document);
    } else {
      this.guilds.insertOne(document);
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
            .append("level", token.getLevel())
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
   * This will listen to when the member data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMemberDataUnloaded(@NotNull BotMemberUnloadedEvent event) {
    Document query =
        new Document("id", event.getData().getId()).append("guildId", event.getData().getGuildId());
    Document first = this.members.find(query).first();
    Document document =
        new Document("id", event.getData().getId()).append("guildId", event.getData().getGuildId());
    document.put("permissions", this.getPermissionStacksDocument(event.getData()));
    document.put("stats", new Document());
    document.get("stats", Document.class).putAll(event.getData().getLinks());
    document.put("links", new Document());
    document.get("links", Document.class).putAll(event.getData().getLinks());
    if (first != null) {
      this.members.replaceOne(query, document);
    } else {
      this.members.insertOne(document);
    }
  }

  /**
   * Get the document of a permission stack from a permissible
   *
   * @param permissible the permissible to get the stacks from
   * @return the document of the permission stack
   */
  @NotNull
  private List<Document> getPermissionStacksDocument(@NotNull Permissible permissible) {
    List<Document> stack = new ArrayList<>();
    for (PermissionStack permission : permissible.getPermissions()) {
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
    Document document =
        new Document("id", event.getData().getId())
            .append("permissions", this.getPermissionStacksDocument(event.getData()))
            .append("lang", event.getData().getLang());
    if (first != null) {
      this.users.replaceOne(query, document);
    } else {
      this.users.insertOne(document);
    }
  }

  /**
   * Listens to an unlinked member being unloaded to save it to the database
   *
   * @param event the event of the member being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onUnlinkedMemberUnloaded(@NotNull BotUnlinkedMemberUnloadedEvent event) {
    BotUnlinkedMember member = event.getMember();
    Document query =
        new Document("guildId", member.getGuildId())
            .append("key", member.getKey())
            .append("value", member.getValue());
    Document first = this.members.find(query).first();
    Document stats = new Document("stats", new Document());
    stats.putAll(member.getStats());
    Document document =
        new Document("guildId", member.getGuildId())
            .append("key", member.getKey())
            .append("value", member.getValue())
            .append("permissions", this.getPermissionStacksDocument(member))
            .append("stats", stats);
    if (first != null) {
      this.members.replaceOne(query, document);
    } else {
      this.members.insertOne(document);
    }
  }

  /**
   * Get the map of links from a document
   *
   * @param document the document to get the map
   * @return the map of links
   */
  @NotNull
  private HashMap<String, String> getLinks(@NotNull Document document) {
    HashMap<String, String> links = new HashMap<>();
    if (document.get("links") instanceof Document) {
      document
          .get("links", Document.class)
          .forEach(
              ((string, object) -> {
                if (object instanceof String) {
                  links.put(string, (String) object);
                }
              }));
    }
    return links;
  }

  /**
   * Get the stats of a member from a document
   *
   * @param document the document to get the stats from
   * @return the stats from the document
   */
  @NotNull
  private HashMap<String, Double> getStats(@NotNull Document document) {
    HashMap<String, Double> stats = new HashMap<>();
    if (document.get("stats") instanceof Document) {
      document
          .get("stats", Document.class)
          .forEach(
              ((string, object) -> {
                if (object instanceof Double) {
                  stats.put(string, (Double) object);
                }
              }));
    }
    return stats;
  }

  /**
   * Get the permission stack from a document
   *
   * @param document the document to get the stack from
   * @return the set of the permission stacks
   */
  @NotNull
  private Set<PermissionStack> getPermissionStacks(Document document) {
    Set<PermissionStack> permissions = new HashSet<>();
    if (document.get("permissions") instanceof List) {
      for (Document stackDocument : document.getList("permissions", Document.class)) {
        Set<Permission> guidoPermissions = new HashSet<>();
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
      return new GuidoGuild(document.getLong("id"));
    }
    return null;
  }

  /**
   * Get the data of a member using a query
   *
   * @param query the query to get the member data
   * @return the member data
   */
  public @Nullable GuidoMember getMemberData(@NotNull Document query) {
    Document document = this.members.find(query).first();
    if (document != null) {
      return new GuidoMember(
          document.getLong("id"),
          document.getLong("guildId"),
          this.getPermissionStacks(document),
          this.getStats(document),
          this.getLinks(document));
    }
    return null;
  }

  /**
   * Get the data of a member using a query
   *
   * @param query the query to get the member data
   * @return the member data
   */
  public @Nullable GuidoUnlinkedMember getUnlinkeedMemberData(@NotNull Document query) {
    Document document = this.members.find(query).first();
    if (document != null) {
      return new GuidoUnlinkedMember(
          document.getString("key"),
          document.getString("value"),
          document.getLong("guildId"),
          this.getPermissionStacks(document),
          this.getStats(document));
    }
    return null;
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
      return new GuidoUser(
          document.getLong("id"),
          this.getPermissionStacks(document),
          this.getPreferences(document));
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
   * @return the token if found in the database else null
   */
  public @Nullable AuthToken getAuthToken(@NotNull Document query) {
    Document document = this.tokens.find(query).first();
    if (document != null) {
      return new GuidoAuthToken(
          document.getString("token"),
          AuthLevel.valueOf(document.getString("level")),
          this.getUserData(document.getLong("user")));
    }
    return null;
  }

  @Override
  public @NotNull BotGuild getGuildData(long id) {
    GuidoGuild guild =
        Cache.getCatchable(
            catchable -> catchable instanceof GuidoGuild && ((GuidoGuild) catchable).getId() == id,
            GuidoGuild.class);
    if (guild != null) {
      return guild;
    }
    guild = this.getGuildData(new Document("id", id));
    if (guild != null) {
      return guild;
    }
    return new GuidoGuild(id);
  }

  @Override
  public @NotNull BotMember getMemberData(long id, long guildId) {
    GuidoMember member =
        Cache.getCatchable(
            catchable ->
                catchable instanceof GuidoMember
                    && ((GuidoMember) catchable).getId() == id
                    && ((GuidoMember) catchable).getGuildId() == guildId,
            GuidoMember.class);
    if (member != null) {
      return member;
    }
    member = this.getMemberData(new Document("id", id).append("guildId", guildId));
    if (member != null) {
      return member;
    }
    return new GuidoMember(id, guildId, new HashSet<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    GuidoRole role =
        Cache.getCatchable(
            catchable ->
                catchable instanceof GuidoRole
                    && ((GuidoRole) catchable).getId() == id
                    && ((GuidoRole) catchable).getGuildId() == guildId,
            GuidoRole.class);
    if (role != null) {
      return role;
    }
    role = this.getRoleData(new Document("id", id).append("guildId", guildId));
    if (role != null) {
      return role;
    }
    return new GuidoRole(id, guildId, new HashSet<>());
  }

  @Override
  public @NotNull BotUser getUserData(long id) {
    GuidoUser user =
        Cache.getCatchable(
            catchable -> catchable instanceof GuidoUser && ((GuidoUser) catchable).getId() == id,
            GuidoUser.class);
    if (user != null) {
      return user;
    }
    user = this.getUserData(new Document("id", id));
    if (user != null) {
      return user;
    }
    return new GuidoUser(id, new HashSet<>(), new GuidoValuesMap());
  }

  @Override
  public @Nullable BotMember getMemberByLink(
      long guildId, @NotNull String key, @NotNull String value) {
    GuidoMember member =
        Cache.getCatchable(
            catchable -> {
              if (catchable instanceof GuidoMember) {
                String link = ((GuidoMember) catchable).getLinks().get(key);
                return ((GuidoMember) catchable).getGuildId() == guildId
                    && link != null
                    && link.equalsIgnoreCase(value);
              }
              return false;
            },
            GuidoMember.class);
    if (member != null) {
      return member;
    }
    GuidoUnlinkedMember unlinkedMember =
        Cache.getCatchable(
            catchable ->
                catchable instanceof GuidoUnlinkedMember
                    && ((GuidoUnlinkedMember) catchable).getKey().equalsIgnoreCase(key)
                    && ((GuidoUnlinkedMember) catchable).getValue().equalsIgnoreCase(value),
            GuidoUnlinkedMember.class);
    if (unlinkedMember != null) {
      return unlinkedMember;
    }
    member = this.getMemberData(new Document("guildId", guildId).append("links." + key, value));
    if (member != null) {
      return member;
    }
    return this.getUnlinkeedMemberData(
        new Document("guildId", guildId).append("key", key).append("value", value));
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    return this.getAuthToken(new Document("token", token));
  }

  @Override
  public void deleteUnlinked(@NotNull UnlinkedMember member) {
    Document query =
        new Document("guildId", member.getGuildId())
            .append("key", member.getKey())
            .append("value", member.getValue());
    this.members.deleteOne(query);
  }
}
