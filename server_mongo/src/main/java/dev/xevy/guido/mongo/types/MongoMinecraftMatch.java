package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoMinecraftMatchLoader;
import dev.xevy.guido.mongo.types.mappers.MinecraftMatchTeamMapper;
import dev.xevy.guido.mongo.types.mappers.MinecraftMatchTeamMemberMapper;
import java.util.*;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.events.match.MinecraftMatchAddTeamEvent;
import me.googas.api.events.match.MinecraftMatchSetTeamsEvent;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.Lots;
import me.googas.api.utility.UUIDUtils;
import me.googas.starbox.logging.LoggerFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftMatch implements MinecraftMatch {

  @NonNull private static final Logger logger = LoggerFactory.getLogger(MongoMinecraftMatch.class);

  @NonNull private final MongoMinecraftMatchLoader loader;
  @NonNull private MongoMinecraftMatch.Document document;

  public MongoMinecraftMatch(
      @NonNull MongoMinecraftMatchLoader loader, @NonNull MongoMinecraftMatch.Document document) {
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
    this.loader.setServer(this, name).ifPresent(doc -> this.document = doc);
  }

  @Override
  public @NonNull String getServer() {
    return this.document.server;
  }

  @Override
  public @NonNull String getMap() {
    return this.document.map;
  }

  @Override
  public void setMap(@NonNull String mapName) {
    this.loader
        .setMap(this, mapName)
        .ifPresent(
            doc -> {
              this.document = doc;
            });
  }

  @Override
  public int addTeam(@NonNull MinecraftMatchTeam team) {
    MongoMinecraftMatchTeam.Document doc = MinecraftMatchTeamMapper.toDocument(team);
    if (doc.id < 0) {
      doc.id = this.nextTeamId();
    }
    return this.loader
        .addTeam(this, doc)
        .map(
            matchDoc -> {
              this.document = matchDoc;
              MinecraftMatchAddTeamEvent event =
                  new MinecraftMatchAddTeamEvent(this, MinecraftMatchTeamMapper.fromDocument(doc));
              this.loader.getLoader().getRuntime().getListeners().call(event);
              return doc.id;
            })
        .orElse(MatchTeam.FAILED);
  }

  @Override
  public @NonNull List<? extends MongoMinecraftMatchTeam> setTeams(
      @NonNull List<? extends MinecraftMatchTeam> teams) {
    List<MongoMinecraftMatchTeam.Document> docs = new ArrayList<>(teams.size());
    int id = 1;
    for (MinecraftMatchTeam team : teams) {
      MongoMinecraftMatchTeam.Document doc = MinecraftMatchTeamMapper.toDocument(team);
      if (doc.id < 0) {
        doc.id = id;
        id++;
      }
      docs.add(doc);
    }

    return this.loader
        .setTeams(this, docs)
        .map(
            doc -> {
              this.document = doc;
              ImmutableCollection<MongoMinecraftMatchTeam> newTeams = this.getTeams();
              MinecraftMatchSetTeamsEvent event = new MinecraftMatchSetTeamsEvent(this, newTeams);
              this.loader.getLoader().getRuntime().getListeners().call(event);
              return newTeams.copy();
            })
        .orElseGet(ArrayList::new);
  }

  private int nextTeamId() {
    int id = this.document.teams.size();
    boolean exists;
    do {
      exists = false;
      for (MongoMinecraftMatchTeam.Document team : this.document.teams) {
        if (team.id == id) {
          exists = true;
          id++;
          break;
        }
      }
    } while (exists);
    return id;
  }

  @Override
  public void appendDetails(@NonNull EmbedBuilder builder) {
    // TODO localize
    if (!this.document.server.isBlank()) {
      builder.addField("Server", this.document.server, true);
    }
  }

  @Override
  public void finish(int winningTeam) {
    this.loader
        .finish(this, winningTeam)
        .ifPresentOrElse(
            doc -> {
              this.document = doc;
              this.callStatusUpdatedEvent();
            },
            () -> {
              logger.severe(
                  String.format("Failed to finish match %s to %s", this.getId(), winningTeam));
            });
  }

  @Override
  public int indexOf(@NonNull MatchTeam matchTeam) {
    return Lots.indexOfMatching(
        this.document.teams, (thisTeam) -> thisTeam.id == matchTeam.getId());
  }

  @Override
  public void setWinnersDifference(int winnersDifference) {
    this.loader.setWinnersDifference(this, winnersDifference).ifPresent(doc -> this.document = doc);
  }

  @Override
  public void setLosersDifference(int losersDifference) {
    this.loader.setLosersDifference(this, losersDifference).ifPresent(doc -> this.document = doc);
  }

  @Override
  public void setStatus(@NonNull MatchStatus matchStatus) {
    this.loader
        .setStatus(this, matchStatus)
        .ifPresent(
            doc -> {
              this.document = doc;
              this.callStatusUpdatedEvent();
            });
  }

  private void callStatusUpdatedEvent() {
    MinecraftMatchStatusUpdatedEvent event =
        new MinecraftMatchStatusUpdatedEvent(this, this.document.status);
    this.loader.getLoader().getRuntime().getListeners().call(event);
  }

  public static class Document {
    @NonNull @BsonId public UUID id = UUIDUtils.EMPTY;
    @NonNull public List<MongoMinecraftMatchTeam.Document> teams = new ArrayList<>();
    @NonNull public MatchStatus status = MatchStatus.VOIDED;
    public int teamWinner = -1;
    @NonNull public String ladderName = "";
    public int winnersDifference = 0;
    public int losersDifference = 0;
    @NonNull public String server = "";
    @NonNull public String map = "";
  }
}
