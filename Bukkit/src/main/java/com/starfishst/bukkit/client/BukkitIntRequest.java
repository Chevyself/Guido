package com.starfishst.bukkit.client;

import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;

/** A request for an integer */
public class BukkitIntRequest extends BukkitRequest<Integer> {
  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitIntRequest(@NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(Integer.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitIntRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(Integer.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BukkitIntRequest(@NonNull String method) {
    super(Integer.class, method);
  }
}
