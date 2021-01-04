package com.starfishst.bukkit.client.requests;

import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;

public class BukkitIntRequest extends BukkitRequest<Integer> {

  public BukkitIntRequest(@NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(Integer.class, method, parameters);
  }

  public BukkitIntRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(Integer.class, method, parameters);
  }

  public BukkitIntRequest(@NonNull String method) {
    super(Integer.class, method);
  }
}
