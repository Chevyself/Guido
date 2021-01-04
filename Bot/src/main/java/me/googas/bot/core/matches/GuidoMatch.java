package me.googas.bot.core.matches;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.Guido;
import me.googas.bot.api.events.match.MatchAddTeamEvent;
import me.googas.bot.api.events.match.MatchPreAddTeamEvent;
import me.googas.bot.api.events.match.MatchPreRemoveTeamEvent;
import me.googas.bot.api.events.match.MatchRemoveTeamEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.events.match.MatchUnloadedEvent;
import me.googas.bot.api.events.match.MatchWinnersSetEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for a match */
public class GuidoMatch implements Match, BotCatchable {

  @NonNull private final String id;
  private final long guildId;
  @NonNull private final Set<MatchTeam> teams;
  @NonNull private final GuidoLinkedValuesMap details;
  @NonNull private MatchStatus status;
  private int winners;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match happened
   * @param status the status of the match
   * @param teams the matchTeams inside the match
   * @param details the details of the match
   * @param winners the winners of the match
   */
  public GuidoMatch(
      @NonNull String id,
      long guildId,
      @NonNull MatchStatus status,
      @NonNull Set<MatchTeam> teams,
      @NonNull GuidoLinkedValuesMap details,
      int winners) {
    this.id = id;
    this.guildId = guildId;
    this.status = status;
    this.teams = teams;
    this.details = details;
    this.winners = winners;
  }

  /**
   * Create the match in a status of waiting
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match happened
   * @param teams the matchTeams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(
      @NonNull String id,
      long guildId,
      @NonNull Set<MatchTeam> teams,
      @NonNull GuidoLinkedValuesMap details) {
    this(id, guildId, MatchStatus.WAITING, teams, details, -1);
  }

  /**
   * Create the match in a status of waiting
   *
   * @param guildId the id of the guild where the match happened
   * @param teams the matchTeams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(
      long guildId, @NonNull Set<MatchTeam> teams, @NonNull GuidoLinkedValuesMap details) {
    this(Guido.getDataLoader().nextMatchId(), guildId, MatchStatus.WAITING, teams, details, -1);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoMatch() {
    this("", -1, MatchStatus.FINISHED, new HashSet<>(), new GuidoLinkedValuesMap(), -1);
  }

  @Override
  public void onRemove() {
    new MatchUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @NonNull
  @Override
  public MatchStatus getStatus() {
    return this.status;
  }

  @Override
  public @NonNull Collection<MatchTeam> getTeams() {
    return this.teams;
  }

  @Override
  public MatchTeam getWinners() {
    return this.getTeam(this.winners);
  }

  @Override
  public @NonNull GuidoValuesMap getDetails() {
    return this.details;
  }

  @Override
  public void setWinners(MatchTeam winners) {
    new MatchWinnersSetEvent(this, winners);
    if (winners != null) {
      this.winners = winners.getId();
    } else {
      this.winners = -1;
    }
  }

  @Override
  public void setStatus(@NonNull MatchStatus status) {
    if (new MatchStatusUpdatedEvent(this, status).callAndGet()) {
      this.status = status;
    }
  }

  @Override
  public boolean addTeam(@NonNull MatchTeam matchTeam) {
    if (!this.getTeams().contains(matchTeam)
        && new MatchPreAddTeamEvent(this, matchTeam).callAndGet()) {
      new MatchAddTeamEvent(this, matchTeam).call();
      this.getTeams().add(matchTeam);
      MatchTeam participants = this.getTeam(-2);
      if (participants != null) this.removeTeam(participants);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeTeam(@NonNull MatchTeam matchTeam) {
    if (this.getTeams().contains(matchTeam)
        && new MatchPreRemoveTeamEvent(this, matchTeam).callAndGet()) {
      new MatchRemoveTeamEvent(this, matchTeam).call();
      this.getTeams().remove(matchTeam);
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("guildId", this.guildId)
        .append("matchTeams", this.teams)
        .append("details", this.details)
        .append("status", this.status)
        .append("winners", this.winners)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoMatch that = (GuidoMatch) o;
    return Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public @NonNull GuidoMatch cache() {
    return (GuidoMatch) BotCatchable.super.cache();
  }

  @Override
  public Ladder getLadder() {
    String ladderName = this.getDetails().get("ladder", String.class);
    BotGuild guild = Guido.getDataLoader().getGuildData(this.getGuildId());
    if (ladderName == null || guild == null) return null;
    return guild.getLadder(ladderName);
  }
}
