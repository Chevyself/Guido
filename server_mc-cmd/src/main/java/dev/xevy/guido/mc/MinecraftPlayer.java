package dev.xevy.guido.mc;

import java.util.UUID;
import lombok.NonNull;

public interface MinecraftPlayer {
  @NonNull
  UUID getUniqueId();

  @NonNull
  String getLocale();

  @NonNull
  String getNickname();

  @NonNull
  String getIp();
}
