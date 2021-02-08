package com.starfishst.bukkit.util;

import com.starfishst.bukkit.api.Guido;
import lombok.NonNull;
import org.bukkit.Bukkit;

public class Tasks {

  public static void sync(@NonNull Runnable runnable) {
    Bukkit.getScheduler().runTask(Guido.getPlugin(), runnable);
  }
}
