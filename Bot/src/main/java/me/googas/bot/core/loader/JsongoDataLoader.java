package me.googas.bot.core.loader;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.api.events.data.group.GroupUnloadedEvent;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.links.LinkableUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.events.data.token.AuthTokenUnloadedEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.events.match.MatchUnloadedEvent;
import me.googas.bot.api.events.match.team.TeamDataUnloadedEvent;
import me.googas.bot.api.events.punishment.PunishmentUnloadedEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.bot.api.types.loader.BotDataLoader;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.discord.GuidoRole;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.bot.core.links.GuidoLinkableInfo;
import me.googas.bot.core.matches.GuidoMatch;
import me.googas.bot.core.matches.GuidoMatchInfo;
import me.googas.bot.core.matches.team.GuidoTeam;
import me.googas.bot.core.permissions.GuidoGroup;
import me.googas.bot.core.permissions.GuidoGroupInfo;
import me.googas.bot.core.punishment.GuidoPunishment;
import me.googas.bot.core.token.GuidoAuthToken;
import me.googas.bot.core.user.GuidoUser;
import me.googas.bot.core.util.Enums;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import org.bson.Document;

/**
 * A data loader that uses both mongo and json
 *
 * <h1>IMPORTANT Mongo does not support dots '.' in their field names!!</h1>
 */
public class JsongoDataLoader implements BotDataLoader {

  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoCollection<Document> guilds;
  @NonNull @Getter private final MongoCollection<Document> roles;
  @NonNull @Getter private final MongoCollection<Document> users;
  @NonNull @Getter private final MongoCollection<Document> links;
  @NonNull @Getter private final MongoCollection<Document> tokens;
  @NonNull @Getter private final MongoCollection<Document> matches;
  @NonNull private final MongoCollection<Document> groups;
  @NonNull @Getter private final MongoCollection<Document> teams;
  @NonNull @Getter private final MongoCollection<Document> punishments;

  /**
   * Create the mongo data loader
   *
   * @param uri the mongo uri to connect
   * @param databaseName the name of the database
   */
  public JsongoDataLoader(@NonNull String uri, @NonNull String databaseName) {
    this.client = MongoClients.create(uri);
    MongoDatabase database = this.client.getDatabase(databaseName);
    this.guilds = database.getCollection("guilds");
    this.roles = database.getCollection("roles");
    this.users = database.getCollection("users");
    this.links = database.getCollection("links");
    this.tokens = database.getCollection("tokens");
    this.matches = database.getCollection("matches");
    this.groups = database.getCollection("groups");
    this.teams = database.getCollection("teams");
    this.punishments = database.getCollection("punishments");
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotGuildUnloaded(@NonNull BotGuildUnloadedEvent event) {
    Mongo.save(this.guilds, new Document("id", event.getData().getId()), event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchUnloaded(@NonNull MatchUnloadedEvent event) {
    Mongo.save(this.matches, new Document("id", event.getMatch().getId()), event.getMatch());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotRoleUnloaded(@NonNull BotRoleUnloadedEvent event) {
    Mongo.save(this.roles, new Document("id", event.getData().getId()), event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamDataUnloaded(@NonNull TeamDataUnloadedEvent event) {
    Mongo.save(this.teams, new Document("id", event.getData().getId()), event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotUserUnloaded(@NonNull UserUnloadedDataEvent event) {
    Mongo.save(this.users, new Document("id", event.getData().getId()), event.getData());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onGroupUnloadedEvent(@NonNull GroupUnloadedEvent event) {
    Mongo.save(this.groups, new Document("id", event.getGroup().getId()), event.getGroup());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onAuthTokenUnloaded(@NonNull AuthTokenUnloadedEvent event) {
    Mongo.save(this.tokens, new Document("token", event.getToken().getToken()), event.getToken());
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onLinkedDataUnloaded(@NonNull LinkableUnloadedEvent event) {
    Linkable data = event.getData();
    Mongo.save(this.links, Mongo.getQuery(data.getType(), data.getIdentification()), data);
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onPunishmentUnloaded(@NonNull PunishmentUnloadedEvent event) {
    Punishment punishment = event.getPunishment();
    Mongo.save(this.punishments, new Document("id", punishment.getId()), punishment);
  }

  @NonNull
  private UserData getAllDiscordUserData(long discordId) {
    Collection<Linkable> discordData = this.getDiscordData(discordId);
    for (Linkable link : discordData) {
      if (link.getLinkedUser() != null) {
        return link.getLinkedUser();
      }
    }
    return new GuidoUser(this.nextUserId(), new GuidoValuesMap()).cache();
  }

  @Override
  public void close() {
    this.client.close();
  }

  @Override
  public Linkable getLink(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links,
        Mongo.getQuery(type, identification),
        linkable -> linkable.compare(type, identification));
  }

  @Override
  public @NonNull Linkable getDiscordUserData(long userId) {
    return Validate.notNullOrGet(
        this.getLink(LinkableType.DISCORD, new GuidoValuesMap("id", userId)),
        () ->
            new GuidoLinkable(
                    LinkableType.DISCORD,
                    new GuidoValuesMap(),
                    this.getAllDiscordUserData(userId).getId(),
                    new GuidoValuesMap("id", userId),
                    new GuidoValuesMap(),
                    new HashMap<>(),
                    new HashSet<>())
                .cache());
  }

  @Override
  public @NonNull Collection<Linkable> getDiscordData(long userId) {
    return new ArrayList<>(
        Mongo.getMany(
            GuidoLinkable.class,
            this.links,
            new Document("identification.id", userId),
            null,
            -1,
            -1));
  }

  @Override
  public @NonNull BotGuild getGuildDataOrCreate(long id) {
    return Validate.notNullOrGet(
        this.getGuildData(id),
        () ->
            new GuidoGuild(
                    id,
                    new HashSet<>(),
                    new HashSet<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashSet<>())
                .cache());
  }

  @Override
  public BotGuild getGuildData(long id) {
    return Mongo.get(
        GuidoGuild.class, this.guilds, new Document("id", id), guild -> guild.getId() == id);
  }

  @Override
  public Linkable getLink(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links,
        Mongo.getQuery(type, identification, recognition),
        link -> link.compare(type, identification, recognition));
  }

  @Override
  public Linkable getLinkByRecognition(@NonNull LinkableType type, @NonNull ValuesMap recognition) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links,
        Mongo.getRecognitionQuery(type, recognition),
        link -> link.compare(type, recognition));
  }

  @Override
  public @NonNull BotRole getRoleData(long id, long guildId) {
    GuidoRole role =
        Validate.notNullOrGet(
            Mongo.get(
                GuidoRole.class,
                this.roles,
                new Document("id", id).append("guildId", guildId),
                cacheRole -> cacheRole.getId() == id && cacheRole.getGuildId() == guildId),
            () -> new GuidoRole(id, guildId, new HashSet<>()).cache());
    if (role != null) return role;
    throw new IllegalStateException("Role could not be initialized");
  }

  @Override
  public UserData getUserData(String id) {
    if (id == null) return null;
    return Mongo.get(
        GuidoUser.class,
        this.users,
        new Document("id", id),
        user -> user.getId().equalsIgnoreCase(id));
  }

  @Override
  public AuthToken getAuthToken(@NonNull String token) {
    return Mongo.get(
        GuidoAuthToken.class,
        this.tokens,
        new Document("token", token),
        cacheToken -> cacheToken.getToken().equals(token));
  }

  @Override
  public long maxPageLeaderboard(@NonNull Ladder ladder, int size) {
    return this.maxPageLeaderboard(ladder.getName() + "-elo", size);
  }

  @Override
  public long maxPageLeaderboard(@NonNull String stat, int size) {
    return Mongo.count(this.links, new Document("stats." + stat, new Document("$type", 1))) / size;
  }

  @Override
  public Match getMatch(@NonNull String id) {
    return Mongo.get(
        GuidoMatch.class, this.matches, new Document("id", id), match -> match.getId().equals(id));
  }

  @Override
  public @NonNull Collection<Match> getParticipating(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull MatchStatus... status) {
    if (status.length == 0) status = MatchStatus.values();
    Set<MatchStatus> statuses = Lots.set(status);
    Map<String, Object> toMatch = new HashMap<>();
    toMatch.put("linkInfo.type", type.toString());
    identification
        .getMap()
        .forEach((key, value) -> toMatch.put("linkInfo.identification." + key, value));
    return new ArrayList<>(
        Mongo.getMany(
            GuidoMatch.class,
            this.matches,
            new Document("status", new Document("$in", Enums.getNames(status)))
                .append(
                    "teams",
                    new Document(
                        "$elemMatch",
                        new Document("members", new Document("$elemMatch", toMatch)))),
            null,
            -1,
            -1,
            guidoMatch ->
                statuses.contains(guidoMatch.getStatus())
                    && guidoMatch.isParticipating(type, identification)));
  }

  @Override
  public Punishment getPunishment(@NonNull String id) {
    return Mongo.get(
        GuidoPunishment.class,
        this.punishments,
        new Document("id", id),
        punishment -> punishment.getId().equalsIgnoreCase(id));
  }

  @Override
  public Group getGroup(@NonNull String id) {
    return Mongo.get(
        GuidoGroup.class, this.groups, new Document("id", id), group -> group.getId().equals(id));
  }

  @Override
  public long countLinks(LinkableType... types) {
    if (types.length == 0) types = LinkableType.values();
    return Mongo.count(
        this.links, new Document("type", new Document("$in", Enums.getNames(types))));
  }

  @Override
  public boolean deleteGroup(@NonNull String id) {
    Group group = this.getGroup(id);
    if (group != null) {
      try {
        group.unload(false);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return Mongo.delete(this.groups, new Document("id", id));
    }
    return false;
  }

  @Override
  public @NonNull Collection<Group> getGroups() {
    return new ArrayList<>(
        Mongo.getMany(
            GuidoGroup.class, this.groups, new Document(), null, -1, -1, (group) -> true));
  }

  @Override
  public Team getTeam(@NonNull String id) {
    return Mongo.get(
        GuidoTeam.class, this.teams, new Document("id", id), team -> team.getId().equals(id));
  }

  @Override
  public Team getTeamByName(@NonNull String name) {
    return Mongo.get(
        GuidoTeam.class,
        this.teams,
        new Document("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE)),
        team -> team.getName().equalsIgnoreCase(name));
  }

  @Override
  public Team getTeam(@NonNull Linkable linkable) {
    Document query = new Document();
    linkable
        .getIdentification()
        .getMap()
        .forEach((key, value) -> query.append("members.linkInfo.identification." + key, value));
    return Mongo.get(GuidoTeam.class, this.teams, query, team -> team.contains(linkable));
  }

  @Override
  public long maxPageGroups(int size) {
    return Mongo.count(this.groups, new Document()) / size;
  }

  @Override
  public @NonNull Collection<GroupInfo> getGroups(int page, int size) {
    return new ArrayList<>(
        Mongo.getMany(GuidoGroupInfo.class, this.groups, new Document(), null, page, size));
  }

  @Override
  public @NonNull Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull Ladder ladder, int page, int size) {
    if (!(ladder instanceof GlobalLadder)) {
      return this.getLeaderboard(ladder.getName() + "-elo", page, size, false);
    }
    throw new IllegalArgumentException("Leaderboard is not available for global ladder");
  }

  @Override
  public @NonNull Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull String stat, int page, int size, boolean inverted) {
    List<GuidoLinkableInfo> list =
        Mongo.getMany(
            GuidoLinkableInfo.class,
            this.links,
            new Document("stats." + stat, new Document("$type", 1)),
            new Document("stats." + stat, inverted ? 1 : -1),
            page,
            size);
    TreeMap<Integer, LinkableInfo> map = new TreeMap<>();
    int index = page * size + 1;
    for (LinkableInfo linkableInfo : list) {
      map.put(index, linkableInfo);
      index++;
    }
    return map;
  }

  @Override
  public @NonNull Collection<MatchInfo> getMatches(
      int page, int size, @NonNull MatchStatus... statuses) {
    if (statuses.length == 0) statuses = MatchStatus.values();
    return new ArrayList<>(
        Mongo.getMany(
            GuidoMatchInfo.class,
            this.matches,
            new Document("type", new Document("$in", Enums.getNames(statuses))),
            null,
            page,
            size));
  }

  @Override
  public boolean deleteTeam(@NonNull String id) {
    Team team = this.getTeam(id);
    if (team != null) {
      try {
        team.unload(false);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
      return Mongo.delete(this.teams, new Document("id", id));
    }
    return false;
  }

  @Override
  public @NonNull Collection<Linkable> getLinks(@NonNull UserData user) {
    return new ArrayList<>(
        Mongo.getMany(
            GuidoLinkable.class,
            this.links,
            new Document("linked-id", user.getId()),
            null,
            -1,
            -1,
            linkable -> user.equals(linkable.getLinkedUser())));
  }

  @Override
  public Linkable getLink(@NonNull UserData user, @NonNull LinkableType type) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links,
        new Document("linked-id", user.getId()).append("type", type.name()),
        linkable -> linkable.getType() == type && user.equals(linkable.getLinkedUser()));
  }

  @Override
  public Collection<LinkableInfo> getLinks(int page, int limit, @NonNull LinkableType... types) {
    if (types.length == 0) types = LinkableType.values();
    return new ArrayList<>(
        Mongo.getMany(
            GuidoLinkableInfo.class,
            this.links,
            new Document("type", new Document("$in", Enums.getNames(types))),
            null,
            page,
            limit));
  }

  @NonNull
  @Override
  public Collection<AuthToken> getTokens(@NonNull UserData user) {
    return new ArrayList<>(
        Mongo.getMany(
            GuidoAuthToken.class, this.tokens, new Document("user", user.getId()), null, -1, -1));
  }
}
