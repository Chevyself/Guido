package dev.xevy.bukkit;

import java.util.Optional;
import lombok.NonNull;
import me.googas.net.sockets.json.client.JsonClient;

public interface GuidoClientRuntime {

  @NonNull
  Optional<JsonClient> getConnection();

  void sync(@NonNull Runnable runnable);
}
