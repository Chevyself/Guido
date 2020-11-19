package me.googas.bot.core.handlers.responsive.roles;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import java.util.HashSet;
import java.util.Set;
import me.googas.bot.api.types.BotResponsiveMessage;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** This responsive message gives roles to the user that reacts to the message */
public class GiveRoleResponsiveMessage implements BotResponsiveMessage {

  /** The id of the message */
  private final long id;

  /** The reactions to give the roles to the member that reacts */
  @NotNull private final Set<GiveRoleReactionResponse> responses;

  /**
   * Create the responsive message
   *
   * @param id the id of the message
   * @param responses the reactions to give the roles to the member
   */
  public GiveRoleResponsiveMessage(long id, @NotNull Set<GiveRoleReactionResponse> responses) {
    this.id = id;
    this.responses = responses;
  }

  /**
   * Create the responsive message
   *
   * @param message the message to get the id
   * @param responses the reactions to give the roles to the member
   */
  public GiveRoleResponsiveMessage(
      @NotNull Message message, @NotNull Set<GiveRoleReactionResponse> responses) {
    this(message.getIdLong(), responses);
    for (GiveRoleReactionResponse response : responses) {
      this.addReactionResponse(response, message);
    }
  }

  /** @deprecated this constructor may only be used by gson */
  public GiveRoleResponsiveMessage() {
    this(-1, new HashSet<>());
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NotNull Set<ReactionResponse> getReactions() {
    return new HashSet<>(this.responses);
  }

  /**
   * The type of responsive message
   *
   * @return the type of responsive message
   */
  @Override
  public @NotNull String getType() {
    return "give-role";
  }
}
