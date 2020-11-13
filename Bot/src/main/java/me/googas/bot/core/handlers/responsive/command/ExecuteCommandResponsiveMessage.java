package me.googas.bot.core.handlers.responsive.command;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.bot.api.types.BotResponsiveMessage;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** A responsive message that its reactions execute commands */
public class ExecuteCommandResponsiveMessage implements BotResponsiveMessage {

  /** The id of the message */
  private final long id;

  /** The command reactions */
  @NotNull private final Set<SimpleCommandReactionResponse> responses = new HashSet<>();

  /**
   * Create the responsive message
   *
   * @param id the id of the message
   */
  public ExecuteCommandResponsiveMessage(long id) {
    this.id = id;
  }

  /**
   * Create the responsive message according
   *
   * @param message the message that will become the responsive message
   * @param responses the command executions that the message can do
   */
  public ExecuteCommandResponsiveMessage(
      @NotNull Message message, @NotNull Collection<SimpleCommandReactionResponse> responses) {
    this.id = message.getIdLong();
    this.responses.addAll(responses);
    for (SimpleCommandReactionResponse response : responses) {
      this.addReactionResponse(response, message);
    }
  }

  /** @deprecated this may only be used by gson */
  public ExecuteCommandResponsiveMessage() {
    this(-1);
  }

  /**
   * The type of responsive message
   *
   * @return the type of responsive message
   */
  @Override
  public @NotNull String getType() {
    return "execute";
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NotNull Set<ReactionResponse> getReactions() {
    return new HashSet<>(this.responses);
  }
}
