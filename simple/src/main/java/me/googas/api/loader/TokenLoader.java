package me.googas.api.loader;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;

public interface TokenLoader extends DataLoader {
  /**
   * Get an auth token using its unique string
   *
   * @param token the unique string of the token
   * @return the token if found else null
   */
  AuthToken getAuthToken(@NonNull String token);

  /**
   * Get the tokens from an user
   *
   * @param user the user to getId the tokens from
   * @return the tokens gene rated by an user
   */
  @NonNull
  Collection<AuthToken> getTokens(@NonNull UserData user);
}
