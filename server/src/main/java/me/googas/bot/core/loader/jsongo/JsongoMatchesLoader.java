package me.googas.bot.core.loader.jsongo;

import com.mongodb.client.MongoCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.MatchLoader;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.utility.Enums;
import me.googas.api.utility.Lots;
import me.googas.bot.core.util.Mongo;
import org.bson.Document;

public class JsongoMatchesLoader extends SimpleJsongoLoader implements MatchLoader {

  public JsongoMatchesLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @NonNull
  public MongoCollection<Document> matches() {
    return this.getCollection("matches");
  }

  @Override
  public AbstractMatch getMatch(@NonNull String id) {
    return Mongo.get(
        AbstractMatch.class,
        this.matches(),
        new Document("id", id),
        match -> match.getId().equals(id));
  }

  @Override
  public @NonNull Collection<AbstractMatch> getParticipating(
      @NonNull LinkableType type,
      @NonNull Map<String, Object> identification,
      @NonNull MatchStatus... status) {
    if (status.length == 0) status = MatchStatus.values();
    Set<MatchStatus> statuses = Lots.set(status);
    Map<String, Object> toMatch = new HashMap<>();
    toMatch.put("linkInfo.type", type.toString());
    identification.forEach((key, value) -> toMatch.put("linkInfo.identification." + key, value));
    return Mongo.getMany(
        AbstractMatch.class,
        this.matches(),
        new Document("status", new Document("$in", Enums.getNames(statuses)))
            .append(
                "teams",
                new Document(
                    "$elemMatch", new Document("members", new Document("$elemMatch", toMatch)))),
        null,
        -1,
        -1,
        guidoMatch ->
            statuses.contains(guidoMatch.getStatus())
                && guidoMatch.isParticipating(type, identification));
  }

  @Override
  public @NonNull Collection<MatchInfo> getMatches(
      int page, int size, @NonNull MatchStatus... statuses) {
    if (statuses.length == 0) statuses = MatchStatus.values();
    return Mongo.getMany(
        MatchInfo.class,
        this.matches(),
        new Document("status", new Document("$in", Enums.getNames(statuses))),
        null,
        page,
        size);
  }
}
