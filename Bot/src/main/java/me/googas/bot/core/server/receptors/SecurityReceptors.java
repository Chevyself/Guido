package me.googas.bot.core.server.receptors;

import java.util.UUID;
import java.util.logging.Level;
import me.googas.bot.Guido;
import me.googas.bot.core.server.request.BooleanGuidoRequest;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class SecurityReceptors {

  /**
   * Check whether a player is connected thru bungee
   *
   * @param uuid the uuid of the player that is connecting
   * @return true if the player is connected thru bungee-cord
   */
  @Receptor("is-bungee")
  public boolean isBungee(@ParamName("uuid") UUID uuid) {
    try {
      Boolean bol = new BooleanGuidoRequest("is-online", Maps.singleton("uuid", uuid)).send();
      if (bol == null) {
        return false;
      } else {
        return bol;
      }
    } catch (MessengerListenFailException e) {
      Guido.getLogger().log(Level.SEVERE, e, null);
    }
    return false;
  }
}
