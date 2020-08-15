package com.starfishst.guido.responsive.role;

import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/** Gives a role that matches the unicode */
public class RoleGiver extends ResponsiveMessage {

  /**
   * Create the role giver from a database
   *
   * @param id the id of the role giver
   */
  public RoleGiver(long id) {
    super(id, new HashSet<>());
  }

  /**
   * Create the role giver from a message
   *
   * @param message the message to become a role giver
   */
  public RoleGiver(@NotNull Message message) {
    super(message, new HashSet<>());
  }

  /**
   * Adds the information of the roles to give
   *
   * @param rolesInfo the information of the roles to give
   */
  public void addRolesInformation(@NotNull List<RoleInformation> rolesInfo) {
    rolesInfo.forEach(this::addReactionResponse);
  }

  /**
   * Get the information of the roles that this gives
   *
   * @return the information of the eroles that this gives
   */
  @NotNull
  public List<RoleInformation> getRolesInformation() {
    List<RoleInformation> info = new ArrayList<>();
    this.getReactions()
        .forEach(
            reaction -> {
              if (reaction instanceof RoleInformation) {
                info.add((RoleInformation) reaction);
              }
            });
    return info;
  }
}
