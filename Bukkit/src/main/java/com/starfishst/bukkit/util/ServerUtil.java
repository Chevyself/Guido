package com.starfishst.bukkit.util;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class ServerUtil {

  @NonNull
  public static String getIp() {
    Server server = Bukkit.getServer();
    return server.getIp() + ":" + server.getPort();
  }
}
