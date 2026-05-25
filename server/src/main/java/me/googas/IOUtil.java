package me.googas;

import lombok.NonNull;
import me.googas.bungee.GuidoBungee;

public class IOUtil {

  @NonNull
  public static String currentDirectory() {
    if (GuidoBungee.isBungee()) {
      return GuidoBungee.validated().getDataFolder().getPath();
    } else {
      return CoreFiles.currentDirectory();
    }
  }
}
