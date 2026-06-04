package me.googas.api.user;

import java.util.UUID;
import lombok.NonNull;

public interface UserData {

  @NonNull
  UUID getId();
}
