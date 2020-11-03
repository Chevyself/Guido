package com.starfishst.bukkit.client;

import java.util.Map;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;

/** An extension to create a boolean request */
public class BukkitBooleanRequest extends BukkitRequest<Boolean> {

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitBooleanRequest(@NotNull String method, @NotNull MapBuilder<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitBooleanRequest(@NotNull String method, @NotNull Map<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BukkitBooleanRequest(@NotNull String method) {
    super(Boolean.class, method);
  }
}
