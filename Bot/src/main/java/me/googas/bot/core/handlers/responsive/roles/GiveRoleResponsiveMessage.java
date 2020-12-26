package me.googas.bot.core.handlers.responsive.roles;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

/** This responsive message gives roles to the user that reacts to the message */
public class GiveRoleResponsiveMessage implements ResponsiveMesage {

  /** The id of the message */
  private final long id;

  /** The reactions to give the roles to the member that reacts */
  @NonNull private final Set<GiveRoleReactionResponse> responses;

  /**
   * Create the responsive message
   *
   * @param id the id of the message
   * @param responses the reactions to give the roles to the member
   */
  public GiveRoleResponsiveMessage(long id, @NonNull Set<GiveRoleReactionResponse> responses) {
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
      @NonNull Message message, @NonNull Set<GiveRoleReactionResponse> responses) {
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
  public @NonNull Set<ReactionResponse> getReactions() {
    return new HashSet<>(this.responses);
  }

  /**
   * The type of responsive message
   *
   * @return the type of responsive message
   */
  @Override
  public @NonNull String getType() {
    return "give-role";
  }

  @Override
  public void addReactionResponse(@NonNull ReactionResponse response, @NonNull Message message) {
    if (!(response instanceof GiveRoleReactionResponse))
      throw new UnsupportedOperationException(
          "Reaction must be a " + GiveRoleReactionResponse.class);
    this.responses.add((GiveRoleReactionResponse) response);
    String unicode = response.getUnicode();
    if (!unicode.startsWith("U+") && !unicode.startsWith("u+")) {
      List<Emote> emotes = message.getGuild().getEmotesByName(unicode, true);
      if (emotes.isEmpty()) {
        throw new IllegalStateException("There's no emotes with the name " + unicode);
      }
      for (Emote emote : emotes) {
        message.addReaction(emote).queue(ignored -> {}, Discord.exceptionConsumer());
      }
    } else {
      message.addReaction(unicode).queue(ignored -> {}, Discord.exceptionConsumer());
    }
  }
}
