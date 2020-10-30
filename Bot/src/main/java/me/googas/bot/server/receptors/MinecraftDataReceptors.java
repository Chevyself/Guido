package me.googas.bot.server.receptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.bot.Guido;
import me.googas.bot.api.data.BotLinkedData;
import me.googas.bot.handlers.data.types.GuidoLinkedData;
import me.googas.bot.handlers.data.types.maps.GuidoValuesMap;
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
    BotLinkedData data =
        Guido.getDataLoader()
            .getLinkedData(
                LinkedDataType.MINECRAFT,
                new GuidoValuesMap("uuid", trimmed).addValue("nickname", nickname),
                false);
    if (data != null) {
      data.refresh();
      return false;
    } else {
      new GuidoLinkedData(
          true,
          LinkedDataType.MINECRAFT,
          null,
          new GuidoValuesMap("uuid", trimmed).addValue("nickname", nickname),
          new GuidoValuesMap(),
          new HashMap<>(),
          new HashSet<>());
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
    BotLinkedData data =
        Guido.getDataLoader()
            .getLinkedData(
                LinkedDataType.MINECRAFT, new GuidoValuesMap("uuid", UUIDUtils.trim(uuid)), false);
    if (data != null) {
      data.refresh().getIdentification().addValue("nickname", nickname);
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
  public LinkedInfo getInfo(@ParamName("nickname") String nick) {
    BotLinkedData data =
        Guido.getDataLoader()
            .getLinkedData(LinkedDataType.MINECRAFT, new GuidoValuesMap("nickname", nick), false);
    if (data != null) {
      return data.refresh().getInfo();
    }
    return null;
  }
}
