package me.googas.bot.core.server.receptors.links;

import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class MinecraftReceptor {

  @Receptor("minecraft/ip")
  public boolean ip(@ParamName("link") LinkableInfo info, @ParamName("ip") String ip) {
    if (info.getType() != LinkableType.MINECRAFT) {
      return false;
    }
    Linkable link = info.getLink();
    if (link == null) return false;
    link.getRecognition().put("ip", ip);
    return true;
  }

  @Receptor("minecraft/nickname")
  public boolean nickname(
      @ParamName("link") LinkableInfo info, @ParamName("nickname") String nickname) {
    if (info.getType() != LinkableType.MINECRAFT) {
      return false;
    }
    Linkable link = info.getLink();
    if (link == null) return false;
    link.getRecognition().put("nickname", nickname);
    return true;
  }
}
