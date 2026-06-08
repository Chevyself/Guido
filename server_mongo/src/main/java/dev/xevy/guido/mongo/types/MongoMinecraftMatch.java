package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoMinecraftMatchLoader;
import dev.xevy.guido.mongo.types.mappers.MinecraftMatchTeamMapper;
import dev.xevy.guido.mongo.types.mappers.MinecraftMatchTeamMemberMapper;
import java.util.*;
import lombok.NonNull;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.Lots;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftMatch implements MinecraftMatch {

  @NonNull private final MongoMinecraftMatch.Document document;
  @NonNull private final MongoMinecraftMatchLoader loader;

  public MongoMinecraftMatch(
      @NonNull MongoMinecraftMatch.Document document, @NonNull MongoMinecraftMatchLoader loader) {
    this.document = document;
    this.loader = loader;
  }

  @Override
  public @NonNull UUID getId() {
    return this.document.id;
  }

  @Override
  public @NonNull ImmutableCollection<MongoMinecraftMatchTeam> getTeams() {
    return ImmutableCollection.map(this.document.teams, MinecraftMatchTeamMapper::fromDocument);
  }

  @Override
  public @NonNull MatchStatus getStatus() {
    return this.document.status;
  }

  @Override
  public int getTeamWinner() {
    return this.document.teamWinner;
  }

  @Override
  public @NonNull String getLadderName() {
    return this.document.ladderName;
  }

  @Override
  public @NonNull ImmutableCollection<MongoMinecraftMatchTeamMember> getParticipants() {
    List<MongoMinecraftMatchTeamMember> participants = new ArrayList<>();
    for (MongoMinecraftMatchTeam.Document team : this.document.teams) {
      for (MongoMinecraftMatchTeamMember.Document memberDoc : team.members) {
        participants.add(MinecraftMatchTeamMemberMapper.fromDocument(memberDoc));
      }
    }
    return new ImmutableCollection<>(participants);
  }

  @Override
  public int getWinnersDifference() {
    return this.document.winnersDifference;
  }

  @Override
  public int getLosersDifference() {
    return this.document.losersDifference;
  }

  @Override
  public void setServer(@NonNull String name) {
    this.loader.setServer(this, name);
    this.document.server = name;
  }

  @Override
  public void appendDetails(@NonNull EmbedBuilder builder) {
    // TODO localize
    if (this.document.server != null) {
      builder.addField("Server", this.document.server, true);
    }
  }

  @Override
  public void finish(int winningTeam) {
    // TODO finish

  }

  @Override
  public int indexOf(@NonNull MatchTeam matchTeam) {
    return Lots.indexOfMatching(
        this.document.teams, (thisTeam) -> thisTeam.id == matchTeam.getId());
  }

  @Override
  public void setWinnersDifference(int winnersDifference) {
    this.loader.setWinnersDifference(this, winnersDifference);
    this.document.winnersDifference = winnersDifference;
  }

  @Override
  public void setLosersDifference(int losersDifference) {
    this.loader.setLosersDifference(this, losersDifference);
    this.document.losersDifference = losersDifference;
  }

  @Override
  public void setStatus(@NonNull MatchStatus matchStatus) {
    this.loader.setStatus(this, matchStatus);
    this.document.status = matchStatus;
    MinecraftMatchStatusUpdatedEvent event =
        new MinecraftMatchStatusUpdatedEvent(this, this.document.status);
    this.loader.getLoader().getRuntime().getListeners().call(event);
  }

  public static class Document {
    @BsonId public UUID id;
    public List<MongoMinecraftMatchTeam.Document> teams;
    public MatchStatus status;
    public int teamWinner;
    public String ladderName;
    public int winnersDifference;
    public int losersDifference;
    public String server;
  }
}
