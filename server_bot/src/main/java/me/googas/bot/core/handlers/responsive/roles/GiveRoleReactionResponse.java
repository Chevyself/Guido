package me.googas.bot.core.handlers.responsive.roles;

import lombok.NonNull;
import me.googas.starbox.jda.responsive.ReactionResponse;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** When the user reacts to this response they will be given the role */
public class GiveRoleReactionResponse implements ReactionResponse {

  /** The id of the role to give */
  private final long role;

  /** The unicode for users to react and getId the role */
  @NonNull private final String unicode;

  /**
   * Create the reaction response
   *
   * @param role the role to give
   * @param unicode the unicode for the users to react and getId the role
   */
  public GiveRoleReactionResponse(long role, @NonNull String unicode) {
    this.role = role;
    this.unicode = unicode;
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    Role role = event.getGuild().getRoleById(this.role);
    Member member = event.getMember();
    if (role == null || member == null || member.getRoles().contains(role)) return true;
    event.getGuild().addRoleToMember(member, role).queue();
    return true;
  }

  @Override
  public boolean hasUnicode(String unicode) {
    return this.unicode.equals(unicode);
  }
}
