package me.googas.api.loader;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.user.UserData;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public interface UserLoader extends DataLoader {

  @NonNull
  UserData ensureUserData(@NonNull User user);

  @NotNull
  Optional<? extends UserData> getById(@NonNull UUID id);
}
