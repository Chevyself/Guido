package me.googas.server;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import lombok.NonNull;
import me.googas.starbox.ProgramArguments;
import me.googas.starbox.events.ListenerManager;

public interface GuidoServerRuntime extends Closeable {
  /**
   * Gets the arguments of the runtime
   *
   * @return the arguments of the runtime
   */
  @NonNull
  ProgramArguments getArguments();

  /**
   * Gets the current directory of the runtime
   *
   * @return the current directory of the runtime
   */
  @NonNull
  File currentDirectory();

  @NonNull
  InputStream getResource(@NonNull String name);

  @NonNull
  ListenerManager getListeners();
}
