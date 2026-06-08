package dev.xevy.guido.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.xevy.guido.mongo.types.MongoMinecraftMatch;
import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeam;
import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeamMember;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.match.MinecraftMatchLoadedEvent;
import me.googas.api.loader.MinecraftMatchLoader;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;
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
    MongoMinecraftMatch match = new MongoMinecraftMatch(doc, this);
    this.loader.getRuntime().getListeners().call(new MinecraftMatchLoadedEvent(match));
    return match;
  }

  @Override
  public @NonNull Optional<MinecraftMatch> getByRegexId(@NonNull String pattern) {
    MongoMinecraftMatch.Document doc = collection.find(Filters.regex("_id", pattern)).first();
    MongoMinecraftMatch match = doc == null ? null : new MongoMinecraftMatch(doc, this);
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull Collection<? extends MinecraftMatch> getParticipating(
      @NonNull UUID id, MatchStatus... statuses) {
    // TODO
    return new ArrayList<>();
  }

  public void setServer(@NonNull MongoMinecraftMatch mongoMinecraftMatch, @NonNull String name) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftMatch.getId()), Updates.set("server", name));
  }

  public void setWinnersDifference(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, int winnersDifference) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftMatch.getId()),
        Updates.set("winnersDifference", winnersDifference));
  }

  public void setLosersDifference(
      @NonNull MongoMinecraftMatch mongoMinecraftMatch, int losersDifference) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftMatch.getId()),
        Updates.set("losersDifference", losersDifference));
  }

  public void setStatus(MongoMinecraftMatch mongoMinecraftMatch, @NonNull MatchStatus status) {
    this.collection.updateOne(
        Filters.eq("_id", mongoMinecraftMatch.getId()), Updates.set("status", status));
  }
}
