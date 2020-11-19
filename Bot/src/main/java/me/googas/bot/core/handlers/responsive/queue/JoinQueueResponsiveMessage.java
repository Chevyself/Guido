package me.googas.bot.core.handlers.responsive.queue;

import java.util.Collection;
import me.googas.bot.core.handlers.responsive.command.ExecuteCommandResponsiveMessage;
import me.googas.bot.core.handlers.responsive.command.SimpleCommandReactionResponse;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** A responsive message which makes an user join a queue */
public class JoinQueueResponsiveMessage extends ExecuteCommandResponsiveMessage {

  /**
   * Create the responsive message
   *
   * @param id the id of the message
   */
  public JoinQueueResponsiveMessage(long id) {
    super(id);
  }

  /**
   * Create the responsive message according
   *
   * @param message the message that will become the responsive message
   * @param responses the command executions that the message can do
   */
  public JoinQueueResponsiveMessage(
      @NotNull Message message, @NotNull Collection<SimpleCommandReactionResponse> responses) {
    super(message, responses);
  }

  /** @deprecated this may only be used by gson */
  public JoinQueueResponsiveMessage() {
    super(-1);
  }

  /**
   * The type of responsive message
   *
   * @return the type of responsive message
   */
  @Override
  public @NotNull String getType() {
    return "queue";
  }
}
