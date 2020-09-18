package com.starfishst.guido.api.implementations.messaging;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** A request that does not expect a response */
public class VoidRequest extends Request {

  /**
   * Create the request
   *
   * @param method the method to identify the type that must return the request
   * @param parameters the parameters of the request
   */
  public VoidRequest(@NotNull String method, @NotNull HashMap<String, Object> parameters) {
    super("void" + method, parameters);
  }

  /**
   * Create the request
   *
   * @param method the method to identify the type that must return the request
   */
  public VoidRequest(@NotNull String method) {
    super("void" + method);
  }
}
