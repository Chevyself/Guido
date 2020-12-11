package me.googas.bot.core.util;

import java.util.logging.Level;
import lombok.NonNull;
import me.googas.bot.core.Guido;
import me.googas.commons.builder.LogBuilder;

public class GuidoLogBuilder extends LogBuilder {
  public GuidoLogBuilder(@NonNull Level level, @NonNull StringBuffer buffer, Object initial) {
    super(level, buffer, initial);
  }

  public GuidoLogBuilder(@NonNull Level level, Object initial) {
    super(level, initial);
  }

  public GuidoLogBuilder(@NonNull Level level) {
    super(level);
  }

  public GuidoLogBuilder(Object initial) {
    super(initial);
  }

  public void send() {
    Guido.getLogger().log(this.build());
  }
}
