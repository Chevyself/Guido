package me.googas.server;

import java.io.File;
import lombok.NonNull;
import me.googas.starbox.ProgramArguments;

public interface GuidoRuntime {
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
}
