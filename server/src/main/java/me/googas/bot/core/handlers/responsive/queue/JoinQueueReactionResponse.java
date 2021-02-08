package me.googas.bot.core.handlers.responsive.queue;

import com.starfishst.commands.jda.CommandManager;
import com.starfishst.commands.jda.listener.CommandListener;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.commands.jda.utils.embeds.EmbedFactory;
import com.starfishst.commands.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.Queueable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.bot.core.handlers.responsive.command.SimpleCommandReactionResponse;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** A message that allows users to join a queue easily */
public class JoinQueueReactionResponse extends SimpleCommandReactionResponse {

  /** The ladder which this message will add the user to */
  @NonNull private final String ladder;

  /**
   * Create the reaction response
   *
   * @param unicode the unicode or the name of the emote that executes it
   * @param ladder the ladder which the user will join
   */
  public JoinQueueReactionResponse(@NonNull String unicode, @NonNull String ladder) {
    super("queue", unicode, Lots.array(ladder));
    this.ladder = ladder;
  }

  /** @deprecated This constructor may only be used by gson */
  public JoinQueueReactionResponse() {
    this("", "");
  }

  /**
   * Build the message displaying who's playing in the ladder
   *
   * @param guild the guild requesting the message
   * @return the built message
   */
  public EmbedQuery buildMessage(@NonNull Guild guild) {
    CommandManager manager = Guido.getCommandManager();
    CommandListener listener = manager.getListener();
    EmbedQuery query = EmbedFactory.fromResult(new Result(""), listener, null);

    StringBuilder participants = Strings.getBuilder();
    GuidoGuild guildData = Guido.getHandlers().getDiscordLoader().getGuild(guild.getIdLong());
    Ladder ladder = guildData.getLadder(this.ladder);
    if (ladder != null) {
      Queue queue = Guido.getHandlers().getHandler(QueueHandler.class).getQueue(guildData, ladder);
      Collection<Queueable> waiting = queue.getWaiting();
      if (waiting.isEmpty()) {
        participants.append(
            Guido.getHandlers()
                .getLanguageHandler()
                .getDefault()
                .get("iq.empty", Maps.singleton("ladder", ladder.getName())));
      } else {
        for (Queueable queueable : waiting) {
          participants
              .append("\n -")
              .append(queueable.getSingle())
              .append(" Elo: ")
              .append(queueable.getElo("none", ladder));
        }
      }
      query.setTitle("Join the queue for " + ladder.getName());
      query.addField("Currently", String.valueOf(waiting.size()), true);
      query.addField("Needed", String.valueOf(ladder.playersPerTeam() * 2), true);
    }
    query.addField("Waiting", participants.toString(), false);
    return query;
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    boolean bol = super.onReaction(event);
    event
        .getChannel()
        .editMessageById(
            event.getMessageIdLong(),
            this.buildMessage(event.getGuild()).getAsMessageQuery().build())
        .queue();
    return bol;
  }
}
