package me.googas.bot.core.handlers.responsive.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.starbox.jda.responsive.ReactionResponse;
import me.googas.starbox.jda.responsive.ResponsiveMessage;
import net.dv8tion.jda.api.entities.Message;

/** A responsive message that its reactions execute commands */
public class ExecuteCommandResponsiveMessage implements ResponsiveMesage {

  /** The id of the message */
  private final long id;

  /** The command reactions */
  @NonNull private final Set<SimpleCommandReactionResponse> responses = new HashSet<>();

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
      @NonNull Message message, @NonNull Collection<SimpleCommandReactionResponse> responses) {
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
  public @NonNull String getType() {
    return "execute";
  }

  @Override
  public @NonNull Collection<ReactionResponse> getReactions(@NonNull String unicode) {
    return this.responses.stream()
        .filter(response -> response.getUnicode().equals(unicode))
        .collect(Collectors.toSet());
  }

  @Override
  public @NonNull ResponsiveMessage addReactionResponse(@NonNull ReactionResponse response) {
    if (response instanceof SimpleCommandReactionResponse) {
      this.responses.add((SimpleCommandReactionResponse) response);
    }
    return this;
  }

  @Override
  public long getId() {
    return this.id;
  }
}
