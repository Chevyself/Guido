package com.starfishst.guido.api.implementations.messaging.json.requests;

import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import java.util.HashMap;

/** Sent to a client when it gets disconnected from the server */
public class DisconnectedRequest extends VoidRequest {

  /** Create the request */
  public DisconnectedRequest() {
    super("disconnected", new HashMap<>());
  }
}
