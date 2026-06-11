package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.links.NamedMinecraftLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.user.UserData;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.server.GuidoGuild;
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
  @Parent
  @Command(
      aliases = {"queue", "play", "jugar"},
      description = "queue.desc")
  public Result queue(
      UserData data,
      LocaleFile locale,
      GuidoGuild guild,
      Member member,
      @Required(name = "queue.ladder", description = "queue.ladder.desc") Ladder ladder) {
    if (Guido.getHandlers().getHandler(MatchMakingHandler.class).isPlaying(data)) {
      return Result.of(locale.get("queue.already-playing"));
    } else {
      GuildVoiceState state = member.getVoiceState();
      if (state == null || state.getChannel() == null) {
        return Result.of(locale.get("queue.join-voice"));
      } else {
        QueueHandler queues = Guido.getHandlers().getHandler(QueueHandler.class);
        if (queues.isWaiting(member, ladder)) {
          return Result.of(locale.get("queue.already"));
        } else {
          QueueResult join = queues.joinQueue(guild, member, ladder);
          if (join.isCancelled()) return Result.of(join.getReason());
          return Result.of(locale.get("queue.success"));
        }
      }
    }
  }

  @Command(aliases = {"in", "i"})
  public Result in(
      LocaleFile locale,
      GuidoGuild guild,
      GuidoBotRuntime runtime,
      @Required(name = "iq.ladder", description = "iq.ladder.desc") Ladder ladder) {
    Collection<UUID> waiting =
        runtime.getHandlers().getHandler(QueueHandler.class).getQueue(ladder).getWaiting();
    if (waiting.isEmpty()) {
      return Result.of(locale.get("iq.empty", Maps.singleton("ladder", ladder.getName())));
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(locale.get("iq.title", Maps.singleton("ladder", ladder.getName())));
      for (NamedMinecraftLinkable minecraft :
          runtime.getLoader().getMinecraftLinks().getNicknamesFor(waiting)) {
        builder.append("\n - ").append(minecraft.getNickname());
      }
      return Result.of(builder.toString(), Duration.ofSeconds(10));
    }
  }

  @Command(aliases = {"force"})
  @GuidoJdaPermission("force")
  public Result force(
      // TODO localize
      GuidoBotRuntime runtime,
      @Required(name = "force.user", description = "force.user.desc") MinecraftLinkable minecraft,
      @Required(name = "force.ladder", description = "force.ladder.desc") Ladder ladder) {
    QueueResult join =
        runtime.getHandlers().getHandler(QueueHandler.class).getQueue(ladder).join(minecraft);
    if (join.isCancelled()) {
      return Result.of("Cancelled " + join.getReason());
    } else {
      return Result.of("Done");
    }
  }

  /**
   * Create a message to easily join queues
   *
   * @param guild the guild that the message is being created to
   * @param message the message of the command execution
   * @param ladder the ladder to getId the queue and join
   * @param unicode the unicode to react and join
   * @return a result with the created message
   */
  @GuidoJdaPermission("guido.queue-msg")
  @Command(
      aliases = {"queueMsg", "qMsg"},
      description = "Create a message to easily join a queue")
  public Result queueMsg(
      GuidoGuild guild,
      Message message,
      @Required(name = "ladder", description = "The ladder to create the queue join message for")
          Ladder ladder,
      @Free(
              name = "unicode",
              description = "The unicode which the user has to react to oin the queue")
          String unicode) {
    String unicodeToUse;
    if (message.getReactions().isEmpty()) {
      if (unicode == null) {
        return Result.of("If you are not using an emote please include an unicode");
      } else {
        unicodeToUse = unicode;
      }
    } else {
      unicodeToUse = message.getReactions().get(0).getEmoji().getName();
    }
    /*TODO
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
     */
    return null;
  }
}
