package com.starfishst.guido.api.data.loader;

import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.RoleData;
import com.starfishst.guido.api.data.UnlinkedMember;
import com.starfishst.guido.api.data.UserData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
   * @param guildId the guild id from which the data of the member must be gotten
   * @return the data of the member or null if not found
   */
  @NotNull
  MemberData getMemberData(long id, long guildId);

  /**
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @NotNull
  RoleData getRoleData(long id, long guildId);

  /**
   * Load the data of an user. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  @NotNull
  UserData getUserData(long id);

  /**
   * Get a member by a link. This will attempt to get both members linked or unlinked
   *
   * @param guild the id of the guild
   * @param key the key of the link
   * @param value the value of the link
   * @return the member if found or an unlinked member if not found
   */
  @Nullable
  MemberData getMemberByLink(long guild, @NotNull String key, @NotNull String value);

  /**
   * Delete an unlinked member
   *
   * @param member the unlinked member to delete
   */
  void deleteUnlinked(@NotNull UnlinkedMember member);
}
