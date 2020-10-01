package com.starfishst.guido.api.implementations.messaging.json.requests;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.Ping;
import com.starfishst.guido.api.implementations.messaging.Request;
import org.jetbrains.annotations.NotNull;

/** Gets how long a requests is taking */
public class PingRequest extends Request<Ping> {

  /** Create the ping request */
  public PingRequest() {
    super("ping", Maps.singleton("start", System.currentTimeMillis()));
  }

  @Override
  public @NotNull Class<Ping> getClazz() {
    return Ping.class;
  }
}
