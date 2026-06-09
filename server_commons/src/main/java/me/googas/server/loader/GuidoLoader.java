package me.googas.server.loader;

import java.io.Closeable;
import lombok.NonNull;
import me.googas.api.loader.Loader;

public interface GuidoLoader extends Loader, Closeable {
  @NonNull
  GuidoGuildLoader getGuidoGuildLoader();
}
