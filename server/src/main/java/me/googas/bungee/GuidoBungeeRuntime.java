package me.googas.bungee;

import lombok.NonNull;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.server.GuidoServerRuntime;

public interface GuidoBungeeRuntime extends GuidoServerRuntime {
  @NonNull
  JsonClient getClient();
}
