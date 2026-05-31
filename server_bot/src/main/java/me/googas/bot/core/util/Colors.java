package me.googas.bot.core.util;

import java.awt.*;
import lombok.NonNull;

public class Colors {

  @NonNull
  public static Color getColor(@NonNull String string) {
    return new Color(Integer.decode(string));
  }
}
