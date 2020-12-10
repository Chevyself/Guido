package com.starfishst.bungee.core.client.requests;

import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;

/** An extension to create a boolean request */
public class BungeeBooleanRequest extends BungeeRequest<Boolean> {

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeBooleanRequest(@NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeBooleanRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the id of the request
   */
  public BungeeBooleanRequest(@NonNull String method) {
    super(Boolean.class, method);
  }
}
