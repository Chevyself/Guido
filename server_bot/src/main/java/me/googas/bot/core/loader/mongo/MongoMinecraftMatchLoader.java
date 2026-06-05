package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
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
import me.googas.bot.core.loader.mongo.types.MongoMinecraftMatch;
import me.googas.bot.core.loader.types.GenericMinecraftMatchTeam;
import me.googas.bot.core.loader.types.GenericMinecraftMatchTeamMember;
import org.jetbrains.annotations.NotNull;

public class MongoMinecraftMatchLoader extends SimpleMongoLoader implements MinecraftMatchLoader {

  @NonNull @Getter private final MongoLoader loader;
  private final MongoCollection<MongoMinecraftMatch> collection;

  public MongoMinecraftMatchLoader(
      @NonNull MongoLoader loader, MongoCollection<MongoMinecraftMatch> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @NonNull
  private Set<GenericMinecraftMatchTeam> mapTeams(
      @NonNull Collection<? extends MinecraftMatchTeam> teams) {
    return teams.stream()
        .map(
            team -> {
              return new GenericMinecraftMatchTeam(
                  team.getId(), mapMembers(team.getMembers()), team.getName());
            })
        .collect(Collectors.toSet());
  }

  private @NonNull Set<GenericMinecraftMatchTeamMember> mapMembers(
      @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> members) {
    return members.stream()
        .map(
            member -> {
              return new GenericMinecraftMatchTeamMember(member.getId(), member.getRole());
            })
        .collect(Collectors.toSet());
  }

  @NotNull
  @Override
  public MongoMinecraftMatch createMatch(
      @NonNull Collection<? extends MinecraftMatchTeam> teams, @NonNull String ladderName) {
    MongoMinecraftMatch match =
        new MongoMinecraftMatch(
            UUID.randomUUID(), mapTeams(teams), MatchStatus.WAITING, -1, ladderName, 0, 0);
    collection.insertOne(match);
    new MinecraftMatchLoadedEvent(match).call();
    return match;
  }

  @Override
  public @NonNull Optional<MinecraftMatch> getByRegexId(@NonNull String pattern) {
    MongoMinecraftMatch match = collection.find(Filters.regex("_id", pattern)).first();
    return Optional.ofNullable(match);
  }
}
