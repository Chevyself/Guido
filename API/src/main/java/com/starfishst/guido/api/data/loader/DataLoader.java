package com.starfishst.guido.api.data.loader;

import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.discord.GuildData;
import com.starfishst.guido.api.data.discord.RoleData;
import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.token.AuthToken;
import java.util.Collection;
import me.googas.commons.RandomUtils;
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
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @NotNull
  RoleData<?, ?> getRoleData(long id, long guildId);

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  @Nullable
  UserData getUserData(@Nullable String id);

  /**
   * Get an auth token using its unique string
   *
   * @param token the unique string of the token
   * @return the token if found else null
   */
  @Nullable
  AuthToken getAuthToken(@NotNull String token);

  /**
   * Get the links from an user
   *
   * @param user the user to get the links from
   * @return the links
   */
  @NotNull
  Collection<? extends LinkedData<?, ?>> getLinks(@NotNull UserData user);

  /**
   * Get the tokens from an user
   *
   * @param user the user to get the tokens from
   * @return the tokens gene rated by an user
   */
  @NotNull
  Collection<? extends AuthToken> getTokens(@NotNull UserData user);

  /**
   * Get some linked data using identification
   *
   * @param type the type of data to get
   * @param identification the identification to get the linked data from
   * @return the linked data if found else null
   */
  @Nullable
  LinkedData<?, ?> getLinkedData(@NotNull LinkedDataType type, @NotNull ValuesMap identification);

  /**
   * Get a match using its id
   *
   * @param id the id of the match
   * @return the id of the match
   */
  @Nullable
  Match getMatch(@NotNull String id);

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NotNull
  default String nextUserId() {
    String id = RandomUtils.nextString(6);
    UserData user = this.getUserData(id);
    while (user != null) {
      id = RandomUtils.nextString(6);
      user = this.getUserData(id);
    }
    return id;
  }

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NotNull
  default String nextMatchId() {
    String id = RandomUtils.nextString(16);
    Match user = this.getMatch(id);
    while (user != null) {
      id = RandomUtils.nextString(16);
      user = this.getMatch(id);
    }
    return id;
  }
}
