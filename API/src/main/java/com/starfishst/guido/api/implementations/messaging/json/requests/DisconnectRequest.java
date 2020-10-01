package com.starfishst.guido.api.implementations.messaging.json.requests;

import com.starfishst.guido.api.implementations.messaging.VoidRequest;

/** Sent by a client to get disconnected from the server */
public class DisconnectRequest extends VoidRequest {

  /** Create the request */
  public DisconnectRequest() {
    super("disconnect");
  }
}
