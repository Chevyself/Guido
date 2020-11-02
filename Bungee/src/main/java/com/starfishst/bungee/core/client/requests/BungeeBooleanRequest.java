package com.starfishst.bungee.core.client.requests;

import java.util.Map;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;

/** An extension to create a boolean request */
public class BungeeBooleanRequest extends BungeeRequest<Boolean> {

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeBooleanRequest(@NotNull String method, @NotNull MapBuilder<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeBooleanRequest(@NotNull String method, @NotNull Map<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BungeeBooleanRequest(@NotNull String method) {
    super(Boolean.class, method);
  }
}
