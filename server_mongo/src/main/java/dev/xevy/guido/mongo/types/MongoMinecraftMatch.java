package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoMinecraftMatchLoader;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.utility.ImmutableCollection;
import me.googas.starbox.logging.LoggerFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftMatch implements MinecraftMatch {

  private static final Logger logger = LoggerFactory.getLogger(MongoMinecraftMatch.class);

  @NonNull @BsonId @Getter private final UUID id;
  @NonNull private final List<MongoMinecraftMatchTeam> teams;
  @NonNull @Getter private MatchStatus status;
  @Getter private int teamWinner;
  @NonNull @Getter private String ladderName;
  @Getter private int winnersDifference;
  @Getter private int losersDifference;
  private String server;

  private transient MongoMinecraftMatchLoader loader;

  public MongoMinecraftMatch(
      @NonNull UUID id,
      @NonNull List<MongoMinecraftMatchTeam> teams,
      @NonNull MatchStatus status,
      int teamWinner,
      @NonNull String ladderName,
      int winnersDifference,
      int losersDifference) {
    this.id = id;
    this.teams = teams;
    this.status = status;
    this.teamWinner = teamWinner;
    this.ladderName = ladderName;
    this.winnersDifference = winnersDifference;
    this.losersDifference = losersDifference;
  }

  @NonNull
  public MongoMinecraftMatch setLoader(@NonNull MongoMinecraftMatchLoader loader) {
    this.loader = loader;
    return this;
  }

  private boolean noLoader(@NonNull Supplier<String> message) {
    if (this.loader == null) return true;
    logger.log(Level.WARNING, message.get(), new IllegalStateException());
    return false;
  }

  @Override
  public @NonNull ImmutableCollection<MongoMinecraftMatchTeam> getTeams() {
    return new ImmutableCollection<>(teams);
  }

  @Override
  public @NonNull ImmutableCollection<MongoMinecraftMatchTeamMember> getParticipants() {
    List<MongoMinecraftMatchTeamMember> participants = new ArrayList<>();
    for (MongoMinecraftMatchTeam team : teams) {
      for (MongoMinecraftMatchTeamMember member : team.getMembers()) {
        participants.add(member);
      }
    }
    return new ImmutableCollection<>(participants);
  }

  @Override
  public void setServer(@NonNull String name) {
    if (this.noLoader(
        () ->
            String.format(
                "Failed to set server for %s to %s, loader has not been set", this, name))) return;
    this.loader.setServer(this, name);
    this.server = name;
  }

  @Override
  public void appendDetails(@NonNull EmbedBuilder builder) {
    // TODO localize
    if (server != null) {
      builder.addField("Server", server, true);
    }
  }

  @Override
  public void finish(int winningTeam) {
    // TODO finish

  }

  @Override
  public int indexOf(@NonNull MatchTeam matchTeam) {
    if (!(matchTeam instanceof MongoMinecraftMatchTeam)) return -1;
    return this.teams.indexOf(matchTeam);
  }

  @Override
  public void setWinnersDifference(int winnersDifference) {
    if (this.noLoader(
        () ->
            String.format(
                "Failed to set winners difference for %s to %d, loader has not been set",
                this, winnersDifference))) return;
    this.loader.setWinnersDifference(this, winnersDifference);
    this.winnersDifference = winnersDifference;
  }

  @Override
  public void setLosersDifference(int losersDifference) {
    if (this.noLoader(
        () ->
            String.format(
                "Failed to set losers difference for %s to %d, loader has not been set",
                this, losersDifference))) return;
    this.loader.setLosersDifference(this, losersDifference);
    this.losersDifference = losersDifference;
  }

  @Override
  public void setStatus(@NonNull MatchStatus matchStatus) {
    if (this.noLoader(
        () ->
            String.format(
                "Failed to set match status for %s to %s, loader has not been set",
                this, matchStatus))) return;
    this.loader.setStatus(this, matchStatus);
    this.status = matchStatus;
    MinecraftMatchStatusUpdatedEvent event = new MinecraftMatchStatusUpdatedEvent(this, status);
    this.loader.getLoader().getRuntime().getListeners().call(event);
  }
}
