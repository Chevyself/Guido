package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoGuidoGuildLoader;
import dev.xevy.guido.mongo.types.mappers.LadderMapper;
import dev.xevy.guido.mongo.types.mappers.RankRangeMapper;
import java.util.*;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.Lots;
import me.googas.server.GuidoGuild;
import me.googas.server.RankRange;
import org.bson.codecs.pojo.annotations.BsonId;
import org.jetbrains.annotations.NotNull;

public class MongoGuidoGuild implements GuidoGuild {

  public MongoGuidoGuild(
      @NonNull MongoGuidoGuildLoader loader, @NonNull MongoGuidoGuild.Document document) {
    this.document = document;
    this.loader = loader;
  }

  @NonNull private final MongoGuidoGuildLoader loader;
  @NonNull private MongoGuidoGuild.Document document;

  @Override
  public long getId() {
    return document.id;
  }

  @Override
  public @NonNull ImmutableCollection<? extends Ladder> getLadders() {
    return ImmutableCollection.map(document.ladders, LadderMapper::fromDocument);
  }

  @Override
  public @NonNull ImmutableCollection<? extends RankRange> getRanges() {
    return ImmutableCollection.map(document.ranges, RankRangeMapper::fromDocument);
  }

  @Override
  public long getMatchesChannelId() {
    return document.matchesChannelId;
  }

  @Override
  public void setMatchesChannelId(long idLong) {
    this.loader.setMatchesChannelId(this, idLong).ifPresent(doc -> this.document = doc);
  }

  @Override
  public long getMatchesCategoryId() {
    return document.matchesCategoryId;
  }

  @Override
  public void setMatchesCategoryId(long idLong) {
    this.loader.setMatchesCategoryId(this, idLong).ifPresent(doc -> this.document = doc);
  }

  @Override
  public Optional<Ladder> addLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseElo,
      int teamsPerMatch,
      double winMultiplier,
      double loseMultiplier,
      MinecraftTeamSelectionType teamSelectionType) {
    MongoLadder.Document doc = new MongoLadder.Document();
    doc.name = name;
    doc.playersPerTeam = playersPerTeam;
    doc.baseValue = baseElo;
    doc.teamsPerMatch = teamsPerMatch;
    doc.winMultiplier = winMultiplier;
    doc.loseMultiplier = loseMultiplier;
    doc.teamSelectionType = teamSelectionType;
    return this.loader
        .addLadder(this, doc)
        .map(
            guildDoc -> {
              this.document = guildDoc;
              return new MongoLadder(doc);
            });
  }

  @Override
  public boolean removeLadderByName(@NonNull String name) {
    int index =
        Lots.indexOfMatching(document.ladders, ladderDoc -> ladderDoc.name.equalsIgnoreCase(name));
    if (index == -1) return false;
    return this.loader
        .removeLadder(this, index)
        .map(
            doc -> {
              this.document = doc;
              return true;
            })
        .orElse(false);
  }

  @NotNull
  @Override
  public Optional<MongoRankRange> addRange(
      @NonNull String ladderName, @NonNull String name, int min, int max, long roleId) {
    MongoRankRange.Document doc = new MongoRankRange.Document();
    doc.ladder = ladderName;
    doc.name = name;
    doc.min = min;
    doc.max = max;
    doc.roleId = roleId;
    return this.loader
        .addRange(this, doc)
        .map(
            guildDoc -> {
              this.document = guildDoc;
              return new MongoRankRange(doc);
            });
  }

  @Override
  public boolean removeRangeByName(@NonNull String name) {
    int index =
        Lots.indexOfMatching(document.ranges, (rangeDoc) -> rangeDoc.name.equalsIgnoreCase(name));
    if (index == -1) return false;
    return this.loader
        .removeRange(this, index)
        .map(
            doc -> {
              this.document = doc;
              return true;
            })
        .orElse(false);
  }

  @Override
  public long getWaitingVoiceChannelId() {
    return document.waitingVoiceChannelId;
  }

  @Override
  public void setWaitingVoiceChannelId(long idLong) {
    this.loader.setWaitingChannelId(this, idLong).ifPresent(doc -> this.document = doc);
  }

  @Override
  public @NonNull Optional<? extends Ladder> getLadderToJoin(long idLong) {
    String ladderName = this.document.voiceToLadder.get(String.valueOf(idLong));
    if (ladderName == null) return Optional.empty();
    return this.getLadder(ladderName);
  }

  @Override
  public void setLadderToJoin(long idLong, @NonNull Ladder ladder) {
    this.loader
        .setLadderToJoin(this, idLong, ladder.getName())
        .ifPresent(doc -> this.document = doc);
  }

  public static class Document {
    @BsonId public long id = 0;
    @NonNull public List<MongoLadder.Document> ladders = new ArrayList<>();
    @NonNull public List<MongoRankRange.Document> ranges = new ArrayList<>();
    public long matchesChannelId = 0;
    public long matchesCategoryId = 0;
    public long waitingVoiceChannelId = 0;
    public Map<String, String> voiceToLadder = new HashMap<>();
  }
}
