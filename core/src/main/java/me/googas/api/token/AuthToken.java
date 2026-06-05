package me.googas.api.token;

import java.util.UUID;
import lombok.NonNull;

/** Token used by clients to authenticate */
public interface AuthToken {

  @NonNull
  UUID getId();

  @NonNull
  String getToken();

  @NonNull
  UUID getUserId();

  @NonNull
  AuthLevel getAuthLevel();
}
