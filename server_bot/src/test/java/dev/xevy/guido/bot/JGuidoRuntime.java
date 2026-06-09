package dev.xevy.guido.bot;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import lombok.NonNull;
import me.googas.bot.GuidoBot;
import me.googas.server.GuidoServerRuntime;
import me.googas.starbox.ProgramArguments;
import me.googas.starbox.events.ListenerManager;

public class JGuidoRuntime implements GuidoServerRuntime {
  @NonNull private final Path currentDirectory = Path.of(System.getProperty("user.dir"));

  @Override
  public @NonNull ProgramArguments getArguments() {
    return new ProgramArguments();
  }

  @Override
  public @NonNull File currentDirectory() {
    return this.currentDirectory.toFile();
  }

  @Override
  public @NonNull InputStream getResource(@NonNull String name) {
    return Objects.requireNonNull(
        GuidoBot.class.getClassLoader().getResourceAsStream(name),
        String.format("Failed to get resource %s as stream", name));
  }

  @Override
  public @NonNull ListenerManager getListeners() {
    throw new UnsupportedOperationException("Can't provide listeners from parent JUnit runtime");
  }

  @Override
  public void close() {
    // No-op
  }
}
