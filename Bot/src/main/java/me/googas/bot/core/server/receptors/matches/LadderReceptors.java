package me.googas.bot.core.server.receptors.matches;

import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class LadderReceptors {

  @Receptor("ladder")
  public Ladder ladder(@ParamName("name") String name, @ParamName("guild") long guildId) {
    BotGuild data = Guido.getDataLoader().getGuildData(guildId);
    if (data != null) {
      return data.getLadder(name);
    }
    return null;
  }
}
