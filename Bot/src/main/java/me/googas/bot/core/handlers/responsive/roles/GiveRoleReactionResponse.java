package me.googas.bot.core.handlers.responsive.roles;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** When the user reacts to this response they will be given the role */
public class GiveRoleReactionResponse implements ReactionResponse {

  /** The id of the role to give */
  private final long role;

  /** The unicode for users to react and get the role */
  @NotNull private final String unicode;

  /**
   * Create the reaction response
   *
   * @param role the role to give
   * @param unicode the unicode for the users to react and get the role
   */
  public GiveRoleReactionResponse(long role, @NotNull String unicode) {
    this.role = role;
    this.unicode = unicode;
  }

  @Override
  public boolean removeReaction() {
    return true;
  }

  @Override
  public void onReaction(@NotNull MessageReactionAddEvent event) {
    Role role = event.getGuild().getRoleById(this.role);
    Member member = event.getMember();
    if (role == null || member == null || member.getRoles().contains(role)) return;
    event.getGuild().addRoleToMember(member, role).queue();
  }

  @Override
  public @NotNull String getUnicode() {
    return this.unicode;
  }
}
