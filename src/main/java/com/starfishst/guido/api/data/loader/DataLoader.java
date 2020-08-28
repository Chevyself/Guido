package com.starfishst.guido.api.data.loader;

import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.RoleData;
import com.starfishst.guido.api.data.UserData;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/** Loads the data. */
public interface DataLoader {

  /**
   * Load the data of a guild. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @NotNull
  GuildData getGuildData(long id);

  /**
   * Load the data of a member. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the member
   * @param guild the guild from which the data of the member must be gotten
   * @return the data of the member or null if not found
   */
  @NotNull
  MemberData getMemberData(long id, @NotNull Guild guild);

  /**
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guild the guild from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @NotNull
  RoleData getRoleData(long id, @NotNull Guild guild);

  /**
   * Load the data of an user. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  @NotNull
  UserData getUserData(long id);
}
