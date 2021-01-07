package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.Collection;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.matches.queue.Queueable;
import me.googas.api.user.UserData;
import me.googas.bot.api.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.bot.core.handlers.responsive.queue.JoinQueueReactionResponse;
import me.googas.bot.core.handlers.responsive.queue.JoinQueueResponsiveMessage;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

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
    if (Guido.getHandlers().getHandler(MatchMakingHandler.class).isPlaying(data)) {
      return new Result(ResultType.USAGE, locale.get("queue.already-playing"));
    } else {
      GuildVoiceState state = member.getVoiceState();
      if (state == null || state.getChannel() == null) {
        return new Result(ResultType.USAGE, locale.get("queue.join-voice"));
      } else {
        QueueHandler queues = Guido.getHandlers().getHandler(QueueHandler.class);
        if (queues.isWaiting(guild, member, ladder)) {
          return new Result(ResultType.USAGE, locale.get("queue.already"));
        } else {
          QueueResult join = queues.joinQueue(guild, member, ladder);
          if (join.isCancelled()) return new Result(ResultType.ERROR, join.getReason());
          return new Result(locale.get("queue.success"));
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
    Collection<Queueable> waiting =
        Guido.getHandlers().getHandler(QueueHandler.class).getQueue(guild, ladder).getWaiting();
    if (waiting.isEmpty()) {
      return new Result(locale.get("iq.empty", Maps.singleton("ladder", ladder.getName())));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("iq.title", Maps.singleton("ladder", ladder.getName())));
      for (Queueable queueable : waiting) {
        builder.append("\n - ").append(queueable.getSingle());
      }
      return new Result(builder.toString());
    }
  }

  /**
   * Create a message to easily join queues
   *
   * @param guild the guild that the message is being created to
   * @param message the message of the command execution
   * @param ladder the ladder to get the queue and join
   * @param unicode the unicode to react and join
   * @return a result with the created message
   */
  @Command(
      aliases = {"queueMsg", "qMsg"},
      description = "Create a message to easily join a queue",
      node = "guido.queue-msg")
  public Result queueMsg(
      BotGuild guild,
      Message message,
      @Required(name = "ladder", description = "The ladder to create the queue join message for")
          Ladder ladder,
      @Optional(
              name = "unicode",
              description = "The unicode which the user has to react to oin the queue")
          String unicode) {
    String unicodeToUse;
    if (message.getEmotes().isEmpty()) {
      if (unicode == null) {
        return new Result(
            ResultType.USAGE, "If you are not using an emote please include an unicode");
      } else {
        unicodeToUse = unicode;
      }
    } else {
      unicodeToUse = message.getEmotes().get(0).getName();
    }
    return new Result(
        "Be the first to join the queue for " + ladder.getName(),
        msg -> {
          JoinQueueReactionResponse reactionResponse =
              new JoinQueueReactionResponse(unicodeToUse, ladder.getName());
          JoinQueueResponsiveMessage responsiveMessage =
              new JoinQueueResponsiveMessage(msg, Lots.set(reactionResponse));
          guild.getMessages().add(responsiveMessage);
          msg.editMessage(
                  reactionResponse.buildMessage(guild.toDiscord()).getAsMessageQuery().build())
              .queue();
        });
  }
}
