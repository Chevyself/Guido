package com.starfishst.bukkit.client;

import java.util.Map;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;

/** A request for an integer */
public class BukkitIntRequest extends BukkitRequest<Integer> {
  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitIntRequest(@NotNull String method, @NotNull MapBuilder<String, ?> parameters) {
    super(Integer.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitIntRequest(@NotNull String method, @NotNull Map<String, ?> parameters) {
    super(Integer.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BukkitIntRequest(@NotNull String method) {
    super(Integer.class, method);
  }
}
