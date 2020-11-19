package me.googas.bot.core.util;

import java.util.logging.Level;
import me.googas.bot.core.Guido;
import me.googas.commons.builder.LogBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuidoLogBuilder extends LogBuilder {
  public GuidoLogBuilder(
      @NotNull Level level, @NotNull StringBuffer buffer, @Nullable Object initial) {
    super(level, buffer, initial);
  }

  public GuidoLogBuilder(@NotNull Level level, @Nullable Object initial) {
    super(level, initial);
  }

  public GuidoLogBuilder(@NotNull Level level) {
    super(level);
  }

  public GuidoLogBuilder(@Nullable Object initial) {
    super(initial);
  }

  public void send() {
    Guido.getLogger().log(this.build());
  }
}
