package me.googas.bot.core.server.receptors;

import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.api.Guido;
import me.googas.bot.core.handlers.link.LinkHandler;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors for linking two accounts */
public class LinkHandlerReceptors {

  /**
   * Create a link code for the linked given info
   *
   * @param info the information of the link to link
   * @return the link
   */
  @Receptor(Requests.Server.LINK_CODE)
  public String linkCode(@ParamName("link") LinkableInfo info) {
    return Guido.getHandlers().getHandler(LinkHandler.class).createCode(info);
  }
}
