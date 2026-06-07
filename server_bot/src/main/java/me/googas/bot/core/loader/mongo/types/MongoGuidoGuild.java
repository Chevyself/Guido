package me.googas.bot.core.loader.mongo.types;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.DiscordRankRange;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.loader.mongo.MongoGuidoGuildLoader;
import me.googas.bot.core.matches.ladder.GuidoLadder;
import me.googas.bot.core.matches.ladder.PlayableLadder;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;
import org.jetbrains.annotations.NotNull;

public class MongoGuidoGuild implements GuidoGuild {

  private static final Logger logger = LoggerFactory.getLogger(MongoGuidoGuild.class);

  @BsonId @Getter private final long id;
  @NonNull private final List<GuidoLadder> ladders;
  @NonNull private final List<DiscordRankRange> ranges;
  @Getter private long matchesChannelId;
  @Getter private long matchesCategoryId;
  @Getter private long waitingVoiceChannelId;
  private transient MongoGuidoGuildLoader loader;

  public MongoGuidoGuild(long id) {
    this.id = id;
    this.ladders = new ArrayList<>();
    this.ranges = new ArrayList<>();
  }

  @NonNull
  public MongoGuidoGuild setLoader(@NonNull MongoGuidoGuildLoader loader) {
    this.loader = loader;
    return this;
  }

  @Override
  public @NonNull ImmutableCollection<? extends PlayableLadder> getLadders() {
    return new ImmutableCollection<>(this.ladders);
  }

  @Override
  public @NonNull ImmutableCollection<DiscordRankRange> getRanges() {
    return new ImmutableCollection<>(this.ranges);
  }

  private boolean noLoader(@NonNull Supplier<String> message) {
    if (this.loader == null) return true;
    logger.log(Level.WARNING, message.get(), new IllegalStateException());
    return false;
  }

  @Override
  public void setMatchesChannelId(long idLong) {
    if (noLoader(
        () ->
            String.format(
                "Failed to set matches channel id for %s to %d, loader has not been set",
                this, idLong))) return;
    this.loader.setMatchesChannelId(this, idLong);
    this.matchesChannelId = idLong;
  }

  @Override
  public void setMatchesCategoryId(long idLong) {
    if (noLoader(
        () ->
            String.format(
                "Failed to set matches category id for %s to %d, loader has not been set",
                this, idLong))) return;
    this.loader.setMatchesCategoryId(this, idLong);
    this.matchesCategoryId = idLong;
  }

  @Override
  public void addLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseElo,
      int teamsPerMatch,
      double winMultiplier,
      double loseMultiplier,
      MinecraftTeamSelectionType teamSelectionType) {
    if (noLoader(() -> String.format("Failed to add ladder to %s, loader has not been set", this)))
      return;
    GuidoLadder ladder =
        new GuidoLadder(
            name,
            playersPerTeam,
            baseElo,
            teamsPerMatch,
            winMultiplier,
            loseMultiplier,
            teamSelectionType);
    this.loader.addLadder(this, ladder);
    this.ladders.add(ladder);
  }

  @Override
  public void removeLadderByName(@NonNull String name) {
    if (noLoader(
        () ->
            String.format(
                "Failed to remove ladder %s from %s, loader has not been set", name, this))) return;
    Optional<Integer> optional = this.getLadder(name).map(this.ladders::indexOf);
    if (optional.isEmpty()) return;
    int index = optional.get();
    this.loader.removeLadder(this, index);
    this.ladders.remove(index);
  }

  @NotNull
  @Override
  public Optional<DiscordRankRange> addRange(
      @NonNull String ladderName, @NonNull String name, int min, int max, long roleId) {
    if (noLoader(() -> String.format("Failed to add range to %s, loader has not been set", this)))
      return Optional.empty();
    DiscordRankRange range = new DiscordRankRange(ladderName, name, min, max, roleId);
    this.loader.addRange(this, range);
    this.ranges.add(range);
    return Optional.of(range);
  }

  @Override
  public void removeRange(@NonNull DiscordRankRange range) {
    if (noLoader(
        () ->
            String.format(
                "Failed to remove range %s from %s, loader has not been set", range, this))) return;
    int index = this.ranges.indexOf(range);
    this.loader.removeRange(this, index);
    this.ranges.remove(range);
  }

  @Override
  public void setWaitingVoiceChannelId(long idLong) {
    if (noLoader(
        () ->
            String.format(
                "Failed to set waiting channel id for %s to %d, loader has not been set",
                this, idLong))) return;
    this.loader.setWaitingChannelId(this, idLong);
    this.waitingVoiceChannelId = idLong;
  }
}
