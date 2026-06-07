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

  public static class Document {
    @BsonId public long id;
    public List<LadderDocument> ladders;
    public List<RankRangeDocument> ranges;
    public long matchesChannelId;
    public long matchesCategoryId;
    public long waitingVoiceChannelId;
  }

  public static class LadderDocument {
    public String name;
    public int playersPerTeam;
    public int baseValue;
    public int teamsPerMatch;
    public double winMultiplier;
    public double loseMultiplier;
    public MinecraftTeamSelectionType teamSelectionType;
  }

  public static class RankRangeDocument {
    public String ladder;
    public String name;
    public int min;
    public int max;
    public long roleId;
  }

  public MongoGuidoGuild(
      @NonNull MongoGuidoGuild.Document document, @NonNull MongoGuidoGuildLoader loader) {
    this.document = document;
    this.loader = loader;
  }

  @NonNull private final MongoGuidoGuild.Document document;
  @NonNull private final MongoGuidoGuildLoader loader;

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
    this.loader.setMatchesChannelId(this, idLong);
    this.document.matchesChannelId = idLong;
  }

  @Override
  public long getMatchesCategoryId() {
    return document.matchesCategoryId;
  }

  @Override
  public void setMatchesCategoryId(long idLong) {
    this.loader.setMatchesCategoryId(this, idLong);
    this.document.matchesCategoryId = idLong;
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
    MongoLadder ladder =
        new MongoLadder(
            name,
            playersPerTeam,
            baseElo,
            teamsPerMatch,
            winMultiplier,
            loseMultiplier,
            teamSelectionType);
    LadderDocument doc = LadderMapper.toDocument(ladder);
    this.loader.addLadder(this, doc);
    this.document.ladders.add(doc);
    return Optional.of(ladder);
  }

  @Override
  public boolean removeLadderByName(@NonNull String name) {
    int index =
        Lots.indexOfMatching(document.ladders, ladderDoc -> ladderDoc.name.equalsIgnoreCase(name));
    if (index == -1) return false;
    this.loader.removeLadder(this, index);
    this.document.ladders.remove(index);
    return true;
  }

  @NotNull
  @Override
  public Optional<MongoRankRange> addRange(
      @NonNull String ladderName, @NonNull String name, int min, int max, long roleId) {
    MongoRankRange range = new MongoRankRange(ladderName, name, min, max, roleId);
    RankRangeDocument doc = RankRangeMapper.toDocument(range);
    this.loader.addRange(this, doc);
    this.document.ranges.add(doc);
    return Optional.of(range);
  }

  @Override
  public boolean removeRangeByName(@NonNull String name) {
    int index =
        Lots.indexOfMatching(document.ranges, (rangeDoc) -> rangeDoc.name.equalsIgnoreCase(name));
    if (index == -1) return false;
    this.loader.removeRange(this, index);
    this.document.ladders.remove(index);
    return true;
  }

  @Override
  public long getWaitingVoiceChannelId() {
    return document.waitingVoiceChannelId;
  }

  @Override
  public void setWaitingVoiceChannelId(long idLong) {
    this.loader.setWaitingChannelId(this, idLong);
    this.document.waitingVoiceChannelId = idLong;
  }
}
