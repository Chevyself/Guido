package com.starfishst.bungee.core.client.requests;

import java.util.Map;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;

/** An extension to create string requests */
public class BungeeStringRequest extends BungeeRequest<String> {

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeStringRequest(@NotNull String method, @NotNull MapBuilder<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeStringRequest(@NotNull String method, @NotNull Map<String, ?> parameters) {
    super(String.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BungeeStringRequest(@NotNull String method) {
    super(String.class, method);
  }
}
