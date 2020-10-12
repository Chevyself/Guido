package com.starfishst.bot.handlers.deploy;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import org.jetbrains.annotations.NotNull;

/** Listens for certain things that can get deployed like adding a permission to a member */
public class DeployHandler implements GuidoEventHandler {

  /**
   * This method deploys a void request to all the connected clients
   *
   * @param request the request to deploy
   */
  public void deploy(@NotNull VoidRequest request) {
    for (Messenger client : Guido.getServer().getClients()) {
      client.sendRequest(request);
    }
  }

  @Override
  public void close() {}
}
