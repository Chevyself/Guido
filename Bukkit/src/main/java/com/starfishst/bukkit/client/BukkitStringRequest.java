package com.starfishst.bukkit.client;

import java.util.Map;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;

/** An extension to create string requests */
public class BukkitStringRequest extends BukkitRequest<String> {

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitStringRequest(@NotNull String method, @NotNull MapBuilder<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitStringRequest(@NotNull String method, @NotNull Map<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BukkitStringRequest(@NotNull String method) {
    super(String.class, method);
  }
}
