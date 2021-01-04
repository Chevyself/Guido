package com.starfishst.bukkit.client.requests;

import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;

public class BukkitBooleanRequest extends BukkitRequest<Boolean> {

  public BukkitBooleanRequest(@NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  public BukkitBooleanRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  public BukkitBooleanRequest(@NonNull String method) {
    super(Boolean.class, method);
  }
}
