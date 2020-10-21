package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoLinkedInfo;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.bot.handlers.link.LinkHandler;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.Map;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class LinkReceptors {

  /**
   * Create a link code for the linked given info
   *
   * @param type the type of link
   * @param identification the way to identify the link
   * @return the link
   */
  @Receptor(method = "link-code")
  public String linkCode(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification) {
    GuidoLinkedInfo info = new GuidoLinkedInfo(type, new GuidoValuesMap(identification));
    return Guido.getHandler(LinkHandler.class).createCode(info);
  }
}
