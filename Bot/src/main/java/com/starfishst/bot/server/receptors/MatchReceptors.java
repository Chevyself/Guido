package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotMatch;
import me.googas.api.matches.Match;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

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
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      return match.refresh();
    }
    return null;
  }
}
