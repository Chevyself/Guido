package me.googas.bot.core.server.receptors.guild;

import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class LadderReceptors {

  @Receptor("ladder")
  public Ladder getLadder(@ParamName("ladder") String ladder, @ParamName("id") long guild) {
    BotGuild data = Guido.getDataLoader().getGuildData(guild);
    if (data == null) return null;
    return data.getLadder(ladder);
  }
}
