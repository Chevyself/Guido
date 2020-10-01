package com.starfishst.guido.api.implementations.messaging.json.requests;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.implementations.messaging.Request;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** Creates a requests with multiple of them inside */
public class MultiRequest extends Request<List<Request<?>>> {

  /**
   * Create the request
   *
   * @param requests the multiple requests to do
   */
  public MultiRequest(@NotNull List<Request<?>> requests) {
    super("multi", Maps.singleton("requests", requests));
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Class<List<Request<?>>> getClazz() {
    List<Request<?>> list = new ArrayList<>();
    return (Class<List<Request<?>>>) list.getClass();
  }
}
