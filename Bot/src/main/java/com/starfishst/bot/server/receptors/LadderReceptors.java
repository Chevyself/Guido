package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors for ladders */
public class LadderReceptors {

  /**
   * Get a ladder using the name of it and the guild
   *
   * @param name the name of the ladder
   * @param guildId the guild of the ladder
   * @return the ladder
   */
  @Receptor("ladder")
  public Ladder ladder(@ParamName("name") String name, @ParamName("guild") long guildId) {
    GuildData data = Guido.getDataLoader().getGuildData(guildId);
    if (data != null) {
      return data.getLadder(name);
    }
    return null;
  }
}
