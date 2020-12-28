package me.googas.bot.core.server.receptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.commons.UUIDUtils;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors to use in minecraft data links */
public class MinecraftDataReceptors {

  /**
   * Create the minecraft data
   *
   * @param uuid the uuid of the minecraft player
   * @param nickname the uuid of the minecraft player
   * @return true if it was created false otherwise
   */
  @Receptor("create-minecraft")
  public boolean create(@ParamName("uuid") UUID uuid, @ParamName("nickname") String nickname) {
    String trimmed = UUIDUtils.trim(uuid);
    Linkable data =
        Guido.getDataLoader()
            .getLink(
                LinkableType.MINECRAFT,
                new GuidoValuesMap("uuid", trimmed).put("nickname", nickname));
    if (data != null) {
      return false;
    } else {
      new GuidoLinkable(
              LinkableType.MINECRAFT,
              new GuidoValuesMap("nickname", nickname),
              null,
              new GuidoValuesMap("uuid", trimmed),
              new GuidoValuesMap(),
              new HashMap<>(),
              new HashSet<>())
          .cache();
      return true;
    }
  }

  /**
   * Updates the nickname of a player
   *
   * @param uuid the uuid of the minecraft player
   * @param nickname the new nickname of the player
   * @return true if the nickname was updated false otherwise
   */
  @Receptor("update-minecraft-nickname")
  public boolean updateNickname(
      @ParamName("uuid") UUID uuid, @ParamName("nickname") String nickname) {
    Linkable data =
        Guido.getDataLoader()
            .getLink(LinkableType.MINECRAFT, new GuidoValuesMap("uuid", UUIDUtils.trim(uuid)));
    if (data != null) {
      if (!data.getRecognition().getOr("nickname", String.class, "").equalsIgnoreCase(nickname)) {
        data.getRecognition().put("nickname", nickname);
      }
      return true;
    }
    return false;
  }

  /**
   * Get the link info from the matching a nick
   *
   * @param nick the nick to match
   * @return the uuid if matched else null
   */
  @Receptor("get-mc-by-name")
  public LinkableInfo getInfo(@ParamName("nickname") String nick) {
    Linkable data =
        Guido.getDataLoader()
            .getLinkByRecognition(LinkableType.MINECRAFT, new GuidoValuesMap("nickname", nick));
    if (data != null) {
      return data.getInfo();
    }
    return null;
  }
}
