package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.Collection;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Ladder;
import me.googas.api.user.UserData;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.matches.QueueHandler;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
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
      UserData data,
      LocaleFile locale,
      BotGuild guild,
      Member member,
      @Required(name = "queue.ladder", description = "queue.ladder.desc") Ladder ladder) {
    if (Guido.getHandler(MatchMakingHandler.class).isPlaying(data)) {
      return new Result(ResultType.USAGE, locale.get("queue.already-playing"));
    } else {
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

  /**
   * See the people that is in queue
   *
   * @param locale the locale of the sender
   * @param guild the guild that will create the match
   * @param ladder the ladder to create the match
   * @return the people in the queue
   */
  @Command(aliases = {"inQueue", "iq", "q"})
  public Result inQueue(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "iq.ladder", description = "iq.ladder.desc") Ladder ladder) {
    Collection<LinkableInfo> waiting =
        Guido.getHandler(QueueHandler.class).getQueue(guild, ladder).getWaiting();
    if (waiting.isEmpty()) {
      return new Result(locale.get("iq.empty", Maps.singleton("ladder", ladder.getName())));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("iq.title", Maps.singleton("ladder", ladder.getName())));
      for (LinkableInfo info : waiting) {
        LinkableData link = info.getLink();
        if (link instanceof BotLinkableData) {
          builder.append(link.getSingle());
        }
      }
      return new Result(builder.toString());
    }
  }
}
