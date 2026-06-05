package me.googas.api.loader;

import lombok.NonNull;
import me.googas.api.user.UserData;
import net.dv8tion.jda.api.entities.User;

public interface UserLoader extends DataLoader {

  @NonNull
  UserData ensureUserData(@NonNull User user);
}
