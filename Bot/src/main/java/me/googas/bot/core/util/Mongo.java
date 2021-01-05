package me.googas.bot.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.GuidoCatchable;
import me.googas.api.ValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.ranks.RankRange;
import me.googas.bot.Guido;
import me.googas.bot.adapters.LinkedValuesMapAdapter;
import me.googas.bot.adapters.LongMongoAdapter;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.adapters.discord.BotGuildDeserializer;
import me.googas.bot.adapters.links.LinkableDeserializer;
import me.googas.bot.adapters.links.LinkedInfoDeserializer;
import me.googas.bot.adapters.matches.ladder.LadderAdapter;
import me.googas.bot.adapters.matches.team.MatchTeamAdapter;
import me.googas.bot.adapters.matches.team.TeamMemberAdapter;
import me.googas.bot.adapters.messages.ResponsiveMessageAdapter;
import me.googas.bot.adapters.permissions.PermissionAdapter;
import me.googas.bot.adapters.permissions.PermissionStackAdapter;
import me.googas.bot.adapters.ranks.RankRangeAdapter;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.bot.core.links.GuidoLinkableInfo;
import me.googas.bot.core.permissions.GuidoPermission;
import me.googas.commons.cache.MemoryCache;
import org.bson.Document;

/** Static utilities for mongo */
public class Mongo {

  private static final Gson GSON = Mongo.constructGson(true);

  public static Gson constructGson(boolean emptyAsLatest) {
    return Mongo.builderGson(emptyAsLatest).create();
  }

  public static GsonBuilder builderGson(boolean emptyAsLatest) {
    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(GuidoGuild.class, new BotGuildDeserializer())
        .registerTypeAdapter(BotGuild.class, new BotGuildDeserializer())
        .registerTypeAdapter(Linkable.class, new LinkableDeserializer(emptyAsLatest))
        .registerTypeAdapter(GuidoLinkable.class, new LinkableDeserializer(emptyAsLatest))
        .registerTypeAdapter(LinkableInfo.class, new LinkedInfoDeserializer(emptyAsLatest))
        .registerTypeAdapter(GuidoLinkableInfo.class, new LinkedInfoDeserializer(emptyAsLatest))
        .registerTypeAdapter(Ladder.class, new LadderAdapter())
        .registerTypeAdapter(MatchTeam.class, new MatchTeamAdapter())
        .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
        .registerTypeAdapter(ResponsiveMesage.class, new ResponsiveMessageAdapter())
        .registerTypeAdapter(Permission.class, new PermissionAdapter())
        .registerTypeAdapter(PermissionStack.class, new PermissionStackAdapter())
        .registerTypeAdapter(GuidoPermission.class, new PermissionAdapter())
        .registerTypeAdapter(RankRange.class, new RankRangeAdapter())
        .registerTypeAdapter(GuidoLinkedValuesMap.class, new LinkedValuesMapAdapter())
        .registerTypeAdapter(long.class, new LongMongoAdapter())
        .registerTypeAdapter(Long.class, new LongMongoAdapter())
        .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
        .registerTypeAdapter(GuidoValuesMap.class, new ValuesMapAdapter());
  }

  public static void save(
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @NonNull Object object) {
    Document document = Document.parse(Mongo.GSON.toJson(object));
    Document first = collection.find(query).first();
    if (first != null) {
      collection.replaceOne(query, document);
    } else {
      collection.insertOne(document);
    }
  }

  @Nullable
  public static <T extends GuidoCatchable> T get(
      @NonNull Class<T> typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @NonNull Predicate<T> predicate) {
    return Guido.getCache()
        .getOrSupply(typeOfT, predicate, () -> Mongo.getCatchable(typeOfT, collection, query));
  }

  @Nullable
  public static <T extends GuidoCatchable> T getCatchable(
      @NonNull Class<T> typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query) {
    Document first = collection.find(query).first();
    if (first == null) return null;
    T t = Mongo.getObject(typeOfT, first);
    T t1 = Guido.getCache().get(typeOfT, catchable -> catchable.equals(t));
    if (t1 != null) return t1;
    if (t != null) t.cache();
    return t;
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
  public static <T> T getObject(@NonNull Type typeOfT, @NonNull Document document) {
    try {
      return Mongo.GSON.fromJson(document.toJson(), typeOfT);
    } catch (JsonSyntaxException e) {
      return null;
    }
  }

  @NonNull
  public static <T> List<T> getMany(
      @NonNull Class<T> typeOfT,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @Nullable Document sort,
      int page,
      int limit) {
    List<T> list = new ArrayList<>();
    FindIterable<Document> find = collection.find(query);
    if (page != -1 && limit != -1) {
      find.limit(limit).skip(page * limit);
    }
    if (sort != null) {
      find.sort(sort);
    }
    MongoCursor<Document> cursor = find.cursor();
    while (cursor.hasNext()) {
      T obj = Mongo.getObject(typeOfT, cursor.next());
      if (obj != null) {
        list.add(obj);
      }
    }
    return list;
  }

  /**
   * Check whether a catchable is contained inside a list
   *
   * @param list the list to see if contains the catchable
   * @param catchable the catchable to see if it is contained
   * @param <T> the type of the catchable
   * @return true if it is inside the list
   */
  public static <T extends GuidoCatchable> boolean contains(@NonNull List<T> list, T catchable) {
    for (T added : list) {
      if (catchable.equals(added)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Delete an object from a collection with the given query
   *
   * @param collection the collection to delete the object from
   * @param query the way to identify the document
   * @return true if at least 1 document was deleted
   */
  public static boolean delete(
      @NonNull MongoCollection<Document> collection, @NonNull Document query) {
    return collection.deleteMany(query).getDeletedCount() > 0;
  }

  /**
   * Create the query for an object with identification
   *
   * @param type the type of linked data to match
   * @param identification the way to identify the data
   * @return the query as a document
   */
  @NonNull
  public static Document getQuery(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    Document query = new Document("type", type.toString());
    identification.getMap().forEach((key, value) -> query.append("identification." + key, value));
    return query;
  }

  /**
   * Create the query for an object with identification
   *
   * @param type the type of linked data to match
   * @param identification the way to identify the data
   * @return the query as a document
   */
  @NonNull
  public static Document getQuery(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition) {
    Document query = new Document("type", type.toString());
    identification.getMap().forEach((key, value) -> query.append("identification." + key, value));
    recognition
        .getMap()
        .forEach(
            (key, value) ->
                query.append(
                    "recognition." + key,
                    Pattern.compile(value.toString(), Pattern.CASE_INSENSITIVE)));
    return query;
  }

  /**
   * Create the query for an object with identification
   *
   * @param type the type of linked data to match
   * @param recognition the way to identify the data
   * @return the query as a document
   */
  @NonNull
  public static Document getRecognitionQuery(
      @NonNull LinkableType type, @NonNull ValuesMap recognition) {
    Document query = new Document("type", type.toString());
    recognition
        .getMap()
        .forEach(
            (key, value) ->
                query.append(
                    "recognition." + key,
                    Pattern.compile(value.toString(), Pattern.CASE_INSENSITIVE)));
    return query;
  }

  /**
   * Count the amount of documents that there's with a query
   *
   * @param collection the collection to send the query
   * @param query the query to match the object
   * @return the amount of documents found with the query
   */
  public static long count(@NonNull MongoCollection<Document> collection, @NonNull Document query) {
    return collection.countDocuments(query);
  }

  @NonNull
  public static <T extends GuidoCatchable> List<T> getMany(
      @NonNull Class<T> clazz,
      @NonNull MongoCollection<Document> collection,
      @NonNull Document query,
      @Nullable Document sort,
      int page,
      int size,
      @NonNull Predicate<T> predicate) {
    MemoryCache cache = Guido.getCache();
    List<T> many = new ArrayList<>(cache.getMany(clazz, predicate));
    List<T> doc = Mongo.getMany(clazz, collection, query, sort, page, size);
    for (T t : doc) {
      if (!Mongo.contains(doc, t)) {
        many.add(t);
        if (!cache.contains(t)) t.cache();
      }
    }
    return many;
  }
}
