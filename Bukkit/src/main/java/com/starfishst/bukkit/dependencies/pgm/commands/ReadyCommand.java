package com.starfishst.bukkit.dependencies.pgm.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.result.Result;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.maps.Maps;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.countdowns.CountdownContext;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

public class ReadyCommand implements GuidoCommand {

  /** The parties that are ready */
  @NonNull private final Set<Party> partiesReady = new HashSet<>();
  /** Whether the command is enabled */
  private boolean enabled = false;

  @Command(aliases = {"ready", "tmready"})
  public Result ready(BukkitLocaleFile locale, Party party) {
    Match match = party.getMatch();
    if (!match.isRunning() && !match.isFinished()) {
      StartMatchModule module = match.needModule(StartMatchModule.class);
      if (!this.partiesReady.contains(party)) {
        if (party == match.getDefaultParty()) {
          return new Result(locale.get("ready.observing"));
        } else {
          this.partiesReady.add(party);
          match.getCountdown().cancelAll(StartCountdown.class);
          module.forceStartCountdown(this.calculateDuration(match), null);
          Guido.getLanguageHandler()
              .broadcast(
                  "ready.now-ready",
                  Maps.singleton("team", party.getColor() + party.getNameLegacy()));
          return new Result(locale.get("ready.success"));
        }
      } else {
        return new Result(locale.get("ready.already"));
      }
    }
    return new Result(locale.get("ready.not-starting"));
  }

  @Command(aliases = {"unready", "tmunready"})
  public Result unready(BukkitLocaleFile locale, Party party) {
    Match match = party.getMatch();
    if (!match.isRunning() && !match.isFinished()) {
      StartMatchModule module = match.needModule(StartMatchModule.class);
      if (this.partiesReady.contains(party)) {
        if (party == match.getDefaultParty()) {
          return new Result(locale.get("unready.observing"));
        } else {
          this.partiesReady.remove(party);
          match.getCountdown().cancelAll(StartCountdown.class);
          module.forceStartCountdown(this.calculateDuration(match), null);
          Guido.getLanguageHandler()
              .broadcast(
                  "unready.now-unready",
                  Maps.singleton("team", party.getColor() + party.getNameLegacy()));
          return new Result(locale.get("unready.success"));
        }
      } else {
        return new Result(locale.get("unready.not-ready"));
      }
    }
    return new Result(locale.get("unready.not-starting"));
  }

  /** Clear all the parties ready */
  public void clear() {
    this.partiesReady.clear();
  }

  /**
   * Calculate the duration to start the match
   *
   * @param match the match to be started
   * @return the duration
   */
  public Duration calculateDuration(@NonNull Match match) {
    int base = PGMMatchMakingHandler.secondsToStart;
    if (this.partiesReady.isEmpty()) {
      return Duration.ofSeconds(base);
    } else {
      int perParty = base / this.getParticipating(match);
      int seconds = base - (perParty * this.partiesReady.size());
      Duration duration;
      if (seconds <= 10) {
        duration = Duration.ofSeconds(10);
      } else if (seconds <= 20) {
        duration = Duration.ofSeconds(20);
      } else {
        duration = Duration.ofSeconds(seconds);
      }
      CountdownContext countdown = match.getCountdown();
      for (StartCountdown startCountdown : countdown.getAll(StartCountdown.class)) {
        if (startCountdown == null) continue;
        if (countdown.getTimeLeft(startCountdown).toMillis() < duration.toMillis()) {
          duration = countdown.getTimeLeft(startCountdown);
        }
      }
      return duration;
    }
  }

  /**
   * Get the amount of parties participating in a match
   *
   * @param match the match to see the amount of parties participating
   * @return the amount of parties participating
   */
  public int getParticipating(@NonNull Match match) {
    int participating = 0;
    for (Party party : match.getParties()) {
      if (party != match.getDefaultParty() && party instanceof Team) {
        participating++;
      }
    }
    return participating;
  }

  /**
   * Set whether the command is enabled
   *
   * @param bol the new value
   */
  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
  }

  /**
   * Get the name of the command. This is used to enable it or not from the configuration
   *
   * @return the name of the command
   */
  @Override
  public @NonNull String getName() {
    return "ready";
  }

  /**
   * Get whether the command is enabled
   *
   * @return true if the command is enabled
   */
  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
