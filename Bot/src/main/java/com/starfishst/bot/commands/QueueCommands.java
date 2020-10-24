package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.handlers.matches.MatchMakingHandler;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

/** Commands related to the queue */
public class QueueCommands {

  @Command(
      aliases = {"queue", "play", "jugar"},
      description = "queue.desc")
  public Result queue(
      LocaleFile locale,
      BotGuild guild,
      Member member,
      @Required(name = "queue.ladder", description = "queue.ladder.desc") Ladder ladder) {
    GuildVoiceState state = member.getVoiceState();
    if (state == null || state.getChannel() == null) {
      return new Result(ResultType.USAGE, locale.get("queue.join-voice"));
    } else {
      if (Guido.getHandler(MatchMakingHandler.class).joinQueue(guild, member, ladder)) {
        return new Result(locale.get("queue.success"));
      } else {
        return new Result(ResultType.ERROR, locale.get("queue.failed"));
      }
    }
  }
}
