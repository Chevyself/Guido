package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.handlers.matches.QueueHandler;
import com.starfishst.core.annotations.Required;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Ladder;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;

/** Commands related to the queue */
public class QueueCommands {

  /**
   * Makes a player join a queue
   *
   * @param locale the locale of the command sender
   * @param guild the guild in which the player will join the queue
   * @param member the player as a discord member
   * @param ladder the ladder that the member wants to play
   * @return whether the player joined the queue
   */
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
      QueueHandler queues = Guido.getHandler(QueueHandler.class);
      if (queues.isWaiting(guild, member, ladder)) {
        return new Result(ResultType.USAGE, locale.get("queue.already"));
      } else if (queues.joinQueue(guild, member, ladder)) {
        return new Result(locale.get("queue.success"));
      } else {
        return new Result(ResultType.ERROR, locale.get("queue.failed"));
      }
    }
  }
}
