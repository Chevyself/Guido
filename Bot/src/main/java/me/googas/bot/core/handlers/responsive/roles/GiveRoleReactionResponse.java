package me.googas.bot.core.handlers.responsive.roles;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** When the user reacts to this response they will be given the role */
public class GiveRoleReactionResponse implements ReactionResponse {

  /** The id of the role to give */
  private final long role;

  /** The unicode for users to react and get the role */
  @NonNull private final String unicode;

  /**
   * Create the reaction response
   *
   * @param role the role to give
   * @param unicode the unicode for the users to react and get the role
   */
  public GiveRoleReactionResponse(long role, @NonNull String unicode) {
    this.role = role;
    this.unicode = unicode;
  }

  @Override
  public boolean removeReaction() {
    return true;
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
  public @NonNull String getUnicode() {
    return this.unicode;
  }
}
