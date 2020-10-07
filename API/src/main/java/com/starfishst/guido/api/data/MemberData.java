package com.starfishst.guido.api.data;

import com.starfishst.core.utils.cache.ICatchable;
import java.util.Collection;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/**
 * The data for a member. This class represents all the data for certain discord user that is inside
 * a guild. This is called a member
 */
public interface MemberData extends Linkable, Permissible, ICatchable {

  /**
   * Get the unique id of the discord user
   *
   * @return the unique id of the discord user
   */
  long getId();

  /**
   * Get the unique id where this entity is a member
   *
   * @return the unique id of the guild
   */
  long getGuildId();

  /**
   * Get the stats of the member inside the server.
   *
   * <p>Stats must be given like this:
   *
   * <p>- Kills, wins and deaths for a gamemode:
   *
   * <p>gamemode-kills: amount gamemode-deaths: amount gamemode-wins: amount
   *
   * <p>- Elo for a ladder:
   *
   * <p>ladder-elo: elo
   *
   * @return the stats of the member
   */
  @NotNull
  HashMap<String, Double> getStats();

  @Override
  default void addLink(@NotNull UnlinkedMemberData unlinked) {
    Linkable.super.addLink(unlinked);
    for (PermissionStack stack : unlinked.getPermissions()) {
      PermissionStack permissions = this.getPermissions(stack.getContext());
      if (permissions == null) {
        permissions =
            new PermissionStack() {
              @Override
              public @NotNull String getContext() {
                return stack.getContext();
              }

              @Override
              public @NotNull Collection<Permission> getPermissions() {
                return stack.getPermissions();
              }
            };
        this.getPermissions().add(permissions);
      } else {
        permissions.getPermissions().addAll(permissions.getPermissions());
      }
    }
  }
}
