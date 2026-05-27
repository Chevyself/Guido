package me.googas.bot.core.handlers.responsive.roles;

import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.bot.core.util.Discord;
import me.googas.starbox.jda.responsive.ReactionResponse;
import me.googas.starbox.jda.responsive.ResponsiveMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

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
   * @param message the message to getId the id
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
  public Collection<ReactionResponse> getReactions(String unicode) {
    return this.responses.stream()
        .filter(response -> response.getUnicode().equals(unicode))
        .collect(Collectors.toSet());
  }

  @Override
  public ResponsiveMessage addReactionResponse(ReactionResponse response) {
    return null;
  }

  @Override
  public void addReactionResponse(@NonNull ReactionResponse response, @NonNull Message message) {
    if (!(response instanceof GiveRoleReactionResponse))
      throw new UnsupportedOperationException(
          "Reaction must be a " + GiveRoleReactionResponse.class);
    this.responses.add((GiveRoleReactionResponse) response);
    Optional<String> optional = response.getUnicode();
    if (optional.isEmpty()) return;
    String unicode = optional.get();
    if (!unicode.startsWith("U+") && !unicode.startsWith("u+")) {
      List<RichCustomEmoji> emotes = message.getGuild().getEmojisByName(unicode, true);
      if (emotes.isEmpty()) {
        throw new IllegalStateException("There's no emotes with the name " + unicode);
      }
      for (RichCustomEmoji emote : emotes) {
        message.addReaction(emote).queue(ignored -> {}, Discord.exceptionConsumer());
      }
    } else {
      UnicodeEmoji emoji = Emoji.fromUnicode(unicode);
      message.addReaction(emoji).queue(ignored -> {}, Discord.exceptionConsumer());
    }
  }
}
