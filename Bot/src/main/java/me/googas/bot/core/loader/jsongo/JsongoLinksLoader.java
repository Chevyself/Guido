package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.LinksLoader;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.user.UserData;
import me.googas.api.utility.Enums;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.bot.core.links.GuidoLinkableInfo;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoLinksLoader extends SimpleJsongoLoader implements LinksLoader {

  public JsongoLinksLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  private MongoCollection<Document> links() {
    return this.getCollection("links");
  }

  @Override
  public Linkable getLink(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links(),
        Mongo.getQuery(type, identification),
        linkable -> linkable.compare(type, identification));
  }

  @Override
  public Linkable getLink(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links(),
        Mongo.getQuery(type, identification, recognition),
        link -> link.compare(type, identification, recognition));
  }

  @Override
  public Linkable getLinkByRecognition(@NonNull LinkableType type, @NonNull ValuesMap recognition) {
    return Mongo.get(
        GuidoLinkable.class,
        this.links(),
        Mongo.getRecognitionQuery(type, recognition),
        link -> link.compare(type, recognition));
  }

  @Override
  public long maxPageLeaderboard(@NonNull Ladder ladder, int size) {
    return this.maxPageLeaderboard(ladder.getName() + "-elo", size);
  }

  @Override
  public long maxPageLeaderboard(@NonNull String stat, int size) {
    return Mongo.count(this.links(), new Document("stats." + stat, new Document("$type", 1)))
        / size;
  }

  @Override
  public long countLinks(LinkableType... types) {
    if (types.length == 0) types = LinkableType.values();
    return Mongo.count(
        this.links(), new Document("type", new Document("$in", Enums.getNames(types))));
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
            this.links(),
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
  public @NonNull Collection<Linkable> getLinks(@NonNull UserData user) {
    return new ArrayList<>(
        Mongo.getMany(
            GuidoLinkable.class,
            this.links(),
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
        this.links(),
        new Document("linked-id", user.getId()).append("type", type.name()),
        linkable -> linkable.getType() == type && user.equals(linkable.getLinkedUser()));
  }

  @Override
  public Collection<LinkableInfo> getLinks(int page, int limit, @NonNull LinkableType... types) {
    if (types.length == 0) types = LinkableType.values();
    return new ArrayList<>(
        Mongo.getMany(
            GuidoLinkableInfo.class,
            this.links(),
            new Document("type", new Document("$in", Enums.getNames(types))),
            null,
            page,
            limit));
  }
}
