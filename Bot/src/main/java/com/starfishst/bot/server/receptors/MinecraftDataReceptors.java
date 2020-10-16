package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.handlers.data.GuidoLinkedData;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors to use in minecraft data links */
public class MinecraftDataReceptors {

  /**
   * Create the minecraft data
   *
   * @param uuid the uuid of the minecraft player
   * @param nick the uuid of the minecraft player
   * @return true if it was created false otherwise
   */
  @Receptor(method = "create-minecraft")
  public boolean create(
      @ParamName(name = "uuid") UUID uuid, @ParamName(name = "nickname") String nick) {
    BotLinkedData data =
        Guido.getDataLoader()
            .getLinkedData(LinkedDataType.MINECRAFT, new GuidoValuesMap("uuid", UUIDUtils.trim(uuid)));
    if (data != null) {
      return false;
    } else {
      new GuidoLinkedData(
          true,
          LinkedDataType.MINECRAFT,
          null,
          new GuidoValuesMap("uuid", UUIDUtils.trim(uuid)).addValue("nickname", nick),
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
   * @param name the new name of the player
   * @return true if the name was updated false otherwise
   */
  @Receptor(method = "update-nickname")
  public boolean updateNickname(
      @ParamName(name = "uuid") UUID uuid, @ParamName(name = "new-name") String name) {
    BotLinkedData data =
        Guido.getDataLoader()
            .getLinkedData(LinkedDataType.MINECRAFT, new GuidoValuesMap("uuid", uuid));
    if (data != null) {
      data.getIdentification().addValue("nickname", name);
      return true;
    }
    return false;
  }

  /**
   * Get the uuid matching a nick
   *
   * @param nick the nick to match
   * @return the uuid if matched else null
   */
  @Receptor(method = "get-uuid")
  public UUID getUuid(@ParamName(name = "nickname") String nick) {
    BotLinkedData data =
        Guido.getDataLoader()
            .getLinkedData(LinkedDataType.MINECRAFT, new GuidoValuesMap("nickname", nick));
    if (data != null) {
      String trimmed = data.getIdentification().getValue("uuid", String.class);
      if (trimmed != null) {
        return UUIDUtils.untrim(trimmed);
      }
      return null;
    }
    return null;
  }
}
