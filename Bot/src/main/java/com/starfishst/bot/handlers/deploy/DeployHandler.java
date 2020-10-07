package com.starfishst.bot.handlers.deploy;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.events.data.member.BotMemberNewLinkEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.bot.handlers.deploy.deployments.MemberNewLink;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import org.jetbrains.annotations.NotNull;

/** Listens for certain things that can get deployed like adding a permission to a member */
public class DeployHandler implements GuidoEventHandler {

  /**
   * Deploy when a member gets a new link
   *
   * @param event the event of a member getting a new link
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMemberNewLink(@NotNull BotMemberNewLinkEvent event) {
    this.deploy(
        new MemberNewLink(
            event.getKey(),
            event.getValue(),
            event.getData().getId(),
            event.getData().getGuildId()));
  }

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
