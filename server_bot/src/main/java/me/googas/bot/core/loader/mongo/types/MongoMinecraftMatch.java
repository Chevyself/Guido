package me.googas.bot.core.loader.mongo.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.core.loader.types.GenericMinecraftMatchTeam;
import me.googas.bot.core.loader.types.GenericMinecraftMatchTeamMember;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoMinecraftMatch implements MinecraftMatch {

  @NonNull @BsonId @Getter private final UUID id;
  @NonNull private final Set<GenericMinecraftMatchTeam> teams;
  @NonNull @Getter private MatchStatus status;
  @Getter private int teamWinner;
  @NonNull @Getter private String ladderName;
  @Getter private int winnersDifference;
  @Getter private int losersDifference;

  public MongoMinecraftMatch(
      @NonNull UUID id,
      @NonNull Set<GenericMinecraftMatchTeam> teams,
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

  @Override
  public @NonNull ImmutableCollection<GenericMinecraftMatchTeam> getTeams() {
    return new ImmutableCollection<>(teams);
  }

  @Override
  public @NonNull ImmutableCollection<GenericMinecraftMatchTeamMember> getParticipants() {
    List<GenericMinecraftMatchTeamMember> participants = new ArrayList<>();
    for (GenericMinecraftMatchTeam team : teams) {
      for (GenericMinecraftMatchTeamMember member : team.getMembers()) {
        participants.add(member);
      }
    }
    return new ImmutableCollection<>(participants);
  }

  @Override
  public void appendDetails(@NonNull EmbedBuilder builder) {
    // TODO pretty :3
  }
}
