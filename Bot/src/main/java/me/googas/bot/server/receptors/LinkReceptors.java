package me.googas.bot.server.receptors;

import me.googas.api.links.LinkedInfo;
import me.googas.bot.Guido;
import me.googas.bot.handlers.link.LinkHandler;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors for linking two accounts */
public class LinkReceptors {

  /**
   * Create a link code for the linked given info
   *
   * @param info the information of the link to link
   * @return the link
   */
  @Receptor("link-code")
  public String linkCode(@ParamName("info") LinkedInfo info) {
    return Guido.getHandler(LinkHandler.class).createCode(info);
  }
}
