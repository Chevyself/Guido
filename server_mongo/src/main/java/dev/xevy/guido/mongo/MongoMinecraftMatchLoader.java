package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoMinecraftMatch;
import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeam;
import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeamMember;
import dev.xevy.guido.mongo.util.Options;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.MinecraftMatchLoader;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

public class MongoMinecraftMatchLoader extends SimpleMongoLoader implements MinecraftMatchLoader {

  @NonNull @Getter private final MongoLoader loader;
  private final MongoCollection<MongoMinecraftMatch.Document> collection;

  public MongoMinecraftMatchLoader(
      @NonNull MongoLoader loader, MongoCollection<MongoMinecraftMatch.Document> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @NonNull
  private List<MongoMinecraftMatchTeam.Document> mapTeams(
      @NonNull Collection<? extends MinecraftMatchTeam> teams) {
    return teams.stream()
        .map(
            team -> {
              MongoMinecraftMatchTeam.Document doc = new MongoMinecraftMatchTeam.Document();
              doc.id = team.getId();
              doc.members = mapMembers(team.getMembers());
              doc.name = team.getName();
              return doc;
            })
        .collect(Collectors.toList());
  }

  private @NonNull Set<MongoMinecraftMatchTeamMember.Document> mapMembers(
      @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> members) {
    return members.stream()
        .map(
            member -> {
              MongoMinecraftMatchTeamMember.Document doc =
                  new MongoMinecraftMatchTeamMember.Document();
              doc.id = member.getId();
              doc.role = member.getRole();
              return doc;
            })
        .collect(Collectors.toSet());
  }

  @NotNull
  @Override
  public MongoMinecraftMatch createMatch(
      @NonNull Collection<? extends MinecraftMatchTeam> teams, @NonNull String ladderName) {
    MongoMinecraftMatch.Document doc = new MongoMinecraftMatch.Document();
    doc.id = UUID.randomUUID();
    doc.teams = mapTeams(teams);
    doc.status = MatchStatus.WAITING;
    doc.teamWinner = -1;
    doc.ladderName = ladderName;
    collection.insertOne(doc);
    return new MongoMinecraftMatch(this, doc);
  }

  @NonNull
  private Optional<MongoMinecraftMatch> getBy(@NonNull Bson bson) {
    MongoMinecraftMatch.Document doc = collection.find(bson).first();
    MongoMinecraftMatch match = doc == null ? null : new MongoMinecraftMatch(this, doc);
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull Collection<? extends MinecraftMatch> getParticipating(
      @NonNull UUID id, MatchStatus... statuses) {
    // TODO
    return new ArrayList<>();
  }

  @Override
  public @NonNull Optional<MongoMinecraftMatch> getById(@NonNull UUID id) {
    return this.getBy(Filters.eq("_id", id));
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> setServer(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, @NonNull String name) {
    MongoMinecraftMatch.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftMatch.getId()),
            Updates.set("server", name),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> setWinnersDifference(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, int winnersDifference) {
    MongoMinecraftMatch.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftMatch.getId()),
            Updates.set("winnersDifference", winnersDifference),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> setLosersDifference(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, int losersDifference) {
    MongoMinecraftMatch.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftMatch.getId()),
            Updates.set("losersDifference", losersDifference),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> setStatus(
      MongoMinecraftMatch mongoMinecraftMatch, @NonNull MatchStatus status) {
    MongoMinecraftMatch.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftMatch.getId()),
            Updates.set("status", status),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> addTeam(
      MongoMinecraftMatch mongoMinecraftMatch, MongoMinecraftMatchTeam.Document teamDoc) {
    MongoMinecraftMatch.Document updated =
        this.collection.findOneAndUpdate(
            Filters.and(
                Filters.eq("_id", mongoMinecraftMatch.getId()),
                Filters.not(Filters.elemMatch("teams", Filters.eq("id", teamDoc.id)))),
            Updates.push("teams", teamDoc),
            Options.RETURN_AFTER);
    return Optional.ofNullable(updated);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> setTeams(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch,
      @NonNull List<MongoMinecraftMatchTeam.Document> docs) {
    MongoMinecraftMatch.Document updated =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftMatch.getId()),
            Updates.set("teams", docs),
            Options.RETURN_AFTER);
    return Optional.ofNullable(updated);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> setMap(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, @NonNull String mapName) {
    MongoMinecraftMatch.Document doc =
        this.collection.findOneAndUpdate(
            Filters.eq("_id", mongoMinecraftMatch.getId()),
            Updates.set("map", mapName),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }

  @NonNull
  public Optional<MongoMinecraftMatch.Document> finish(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, int winningTeam) {
    MongoMinecraftMatch.Document doc =
        this.collection.findOneAndUpdate(
            Filters.and(
                Filters.eq("_id", mongoMinecraftMatch.getId()),
                Filters.in("status", MatchStatus.FINISHABLE_STATUSES)),
            Updates.combine(
                Updates.set("status", MatchStatus.FINISHED),
                Updates.set("teamWinner", winningTeam)),
            Options.RETURN_AFTER);
    return Optional.ofNullable(doc);
  }
}
