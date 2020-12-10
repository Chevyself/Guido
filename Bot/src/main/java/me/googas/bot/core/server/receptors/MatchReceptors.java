package me.googas.bot.core.server.receptors;

import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.types.GuidoTeam;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.JsonClientThread;

/** Receptors for matches */
public class MatchReceptors {

  /**
   * Query for a match
   *
   * @param id the id of the match to get
   * @return the match
   */
  @Receptor("match")
  public Match match(@ParamName("id") String id) {
    return Guido.getDataLoader().getMatch(id);
  }

  /**
   * Updates the status of the match
   *
   * @param id the id of the match to update the status to
   * @param status the new status of the match
   * @return whether the status of the match has been updated
   */
  @Receptor("match-status")
  public boolean status(@ParamName("id") String id, @ParamName("status") MatchStatus status) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      match.setStatus(status);
      return true;
    }
    return false;
  }

  /**
   * Adds a team to the match
   *
   * @param id the id of the match to add a team to
   * @param team the team to add to ethe match
   * @return whether the team has been set
   */
  @Receptor("match-add-team")
  public int addTeam(@ParamName("id") String id, @ParamName("team") Team team) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      if (team.getId() == -3) {
        GuidoTeam guidoTeam = new GuidoTeam(match.nextTeamId(), team.getMembers(), team.getName());
        if (match.addTeam(guidoTeam)) {
          return guidoTeam.getId();
        }
      } else {
        if (match.addTeam(team)) {
          return team.getId();
        }
      }
    }
    return -4;
  }

  /**
   * Removes a team from the match
   *
   * @param id the id of the match to remove the team from
   * @param teamName the name of the team to remove
   * @return whether the team has been removed
   */
  @Receptor("match-remove-team")
  public boolean removeTeam(@ParamName("id") String id, @ParamName("team") String teamName) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      Team team = match.getTeam(teamName);
      if (team != null) {
        return match.removeTeam(team);
      }
    }
    return false;
  }

  /**
   * Removes a team from the match
   *
   * @param id the id of the match to remove the team from
   * @param teamId the id of the team to remove
   * @return whether the team has been removed
   */
  @Receptor("match-remove-team-by-id")
  public boolean removeTeam(@ParamName("id") String id, @ParamName("team") int teamId) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      Team team = match.getTeam(teamId);
      if (team != null) {
        return match.removeTeam(team);
      }
    }
    return false;
  }

  /**
   * Finishes a match
   *
   * @param id the id of the match to finish
   * @param winners the winners of the match
   * @return whether the match was finished
   */
  @Receptor("match-finish")
  public boolean finish(@ParamName("id") String id, @ParamName("winners") String winners) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      if (winners != null) {
        Team team = match.getTeam(winners);
        if (team != null) {
          match.finish(team);
          return true;
        } else {
          return false;
        }
      } else {
        match.finish(null);
        return true;
      }
    }
    return false;
  }

  /**
   * Finishes a match
   *
   * @param id the id of the match to finish
   * @param winners the id of the winners of the match
   * @return whether the match was finished
   */
  @Receptor("match-finish-id")
  public boolean finish(@ParamName("id") String id, @ParamName("winners") int winners) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      if (winners != -1) {
        Team team = match.getTeam(winners);
        if (team != null) {
          match.finish(team);
          return true;
        } else {
          return false;
        }
      } else {
        match.finish(null);
        return true;
      }
    }
    return false;
  }

  /**
   * Called when a server is ready to receive a new match
   *
   * @param messenger the server that is ready for the new match
   * @return true if received correctly
   */
  @Receptor("server-ready")
  public boolean ready(JsonMessenger messenger) {
    if (messenger instanceof JsonClientThread) {
      Guido.getHandler(MatchMakingHandler.class).serverReady();
      return true;
    }
    return false;
  }
}
