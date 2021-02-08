package me.googas.bot.core.handlers.responsive.command;

import com.starfishst.commands.jda.AnnotatedCommand;
import com.starfishst.commands.jda.CommandManager;
import com.starfishst.commands.jda.context.GuildCommandContext;
import com.starfishst.commands.jda.listener.CommandListener;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.commands.jda.utils.embeds.EmbedFactory;
import com.starfishst.commands.jda.utils.responsive.ReactionResponse;
import gnu.trove.set.hash.TLongHashSet;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.commons.Strings;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

/** A reaction response which will execute a command when reacted */
public interface ExecuteCommandReactionResponse extends ReactionResponse {

  /**
   * Get the name of the command to execute
   *
   * @return the name of the command to execute
   */
  @NonNull
  String getCommandName();

  /**
   * Get the arguments to use in the execution of the command
   *
   * @return the arguments
   */
  @NonNull
  String[] getArguments();

  @Override
  default boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (event.getUser() == null) return true;
    CommandManager manager = Guido.getCommandManager();
    CommandListener listener = manager.getListener();
    TextChannel channel = event.getTextChannel();
    AnnotatedCommand command =
        Validate.notNull(
            manager.getCommand(this.getCommandName()),
            "The command " + this.getCommandName() + " seems that is not registered");
    Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
    Message contextMessage =
        new ReceivedMessage(
            message.getIdLong(),
            message.getTextChannel(),
            message.getType(),
            message.isWebhookMessage(),
            message.mentionsEveryone(),
            new TLongHashSet(),
            new TLongHashSet(),
            message.isTTS(),
            message.isPinned(),
            Strings.fromArray(this.getArguments()),
            "",
            event.getUser(),
            event.getMember(),
            message.getActivity(),
            message.getTimeEdited(),
            message.getReactions(),
            message.getAttachments(),
            message.getEmbeds(),
            0);
    GuildCommandContext context =
        new GuildCommandContext(
            contextMessage,
            event.getUser(),
            this.getArguments(),
            channel,
            null,
            manager.getMessagesProvider(),
            manager.getRegistry(),
            this.getCommandName());
    Result result = command.execute(context);
    if (result != null) {
      EmbedFactory.fromResult(result, listener, context)
          .send(channel, msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
    }
    return true;
  }
}
