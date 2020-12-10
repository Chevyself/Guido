package com.starfishst.bukkit.client;

import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;

/** An extension to create string requests */
public class BukkitStringRequest extends BukkitRequest<String> {

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitStringRequest(@NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitStringRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BukkitStringRequest(@NonNull String method) {
    super(String.class, method);
  }
}
