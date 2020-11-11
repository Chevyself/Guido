package me.googas.bot.core.server.receptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import me.googas.api.links.LinkableDataType;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoLinkableData;
import me.googas.bot.core.types.maps.GuidoValuesMap;
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
    BotLinkableData data =
        Guido.getDataLoader()
            .getLinkedData(
                LinkableDataType.MINECRAFT,
                new GuidoValuesMap("uuid", trimmed).put("nickname", nickname));
    if (data != null) {
      return false;
    } else {
      new GuidoLinkableData(
              LinkableDataType.MINECRAFT,
              null,
              new GuidoValuesMap("uuid", trimmed).put("nickname", nickname),
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
    BotLinkableData data =
        Guido.getDataLoader()
            .getLinkedData(
                LinkableDataType.MINECRAFT, new GuidoValuesMap("uuid", UUIDUtils.trim(uuid)));
    if (data != null) {
      if (!data.getIdentification()
          .getOr("nickname", String.class, "")
          .equalsIgnoreCase(nickname)) {
        data.getIdentification().put("nickname", nickname);
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
    BotLinkableData data =
        Guido.getDataLoader()
            .getLinkedData(LinkableDataType.MINECRAFT, new GuidoValuesMap("nickname", nick));
    if (data != null) {
      return data.getInfo();
    }
    return null;
  }
}
