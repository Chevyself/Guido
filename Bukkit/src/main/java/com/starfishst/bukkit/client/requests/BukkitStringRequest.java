package com.starfishst.bukkit.client.requests;

import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;

public class BukkitStringRequest extends BukkitRequest<String> {

  public BukkitStringRequest(@NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  public BukkitStringRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  public BukkitStringRequest(@NonNull String method) {
    super(String.class, method);
  }
}
