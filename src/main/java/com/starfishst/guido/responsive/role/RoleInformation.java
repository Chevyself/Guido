package com.starfishst.guido.responsive.role;

import com.starfishst.commands.utils.responsive.ReactionResponse;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The information of the reaction to give a role */
public class RoleInformation implements ReactionResponse {

  /** The role giver owner of this role information */
  @NotNull private final transient RoleGiver roleGiver;
  /** The unicode of the emoji */
  @NotNull private final String unicode;
  /** The id of the role */
  private final long roleId;

  /**
   * Create the role information
   *
   * @param roleGiver the role giver to which this is appended
   * @param unicode the unicode for this role
   * @param roleId the id of the role
   */
  public RoleInformation(@NotNull RoleGiver roleGiver, @NotNull String unicode, long roleId) {
    this.roleGiver = roleGiver;
    this.unicode = unicode;
    this.roleId = roleId;
  }

  /**
   * Get the role
   *
   * @param api the api to get the role
   * @return the role
   */
  @Nullable
  public Role getRole(@NotNull JDA api) {
    return api.getRoleById(this.roleId);
  }

  /**
   * Get the id of the role that this gives
   *
   * @return the id of the role
   */
  public long getRoleId() {
    return this.roleId;
  }

  /**
   * Get the unicode
   *
   * @return the unicode
   */
  @NotNull
  @Override
  public String getUnicode() {
    return this.unicode;
  }

  @Override
  public boolean removeReaction() {
    return true;
  }

  @Override
  public void onReaction(@NotNull GuildMessageReactionAddEvent event) {
    Guild guild = event.getGuild();
    this.roleGiver
        .getRolesInformation()
        .forEach(
            roleInformation -> {
              List<Role> memberRoles = event.getMember().getRoles();
              Role role = roleInformation.getRole(event.getJDA());
              if (role != null && memberRoles.contains(role) && roleInformation != this) {
                guild.removeRoleFromMember(event.getMember(), role).queue();
              } else if (role != null && !memberRoles.contains(role) && roleInformation == this) {
                guild.addRoleToMember(event.getMember(), role).queue();
              }
            });
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof RoleInformation)) return false;

    RoleInformation that = (RoleInformation) object;

    if (this.roleId != that.roleId) return false;
    return this.unicode.equals(that.unicode);
  }

  @Override
  public int hashCode() {
    int result = this.unicode.hashCode();
    result = 31 * result + (int) (this.roleId ^ (this.roleId >>> 32));
    return result;
  }
}
