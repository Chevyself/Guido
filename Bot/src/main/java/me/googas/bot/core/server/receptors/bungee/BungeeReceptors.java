package me.googas.bot.core.server.receptors.bungee;

import java.util.UUID;
import me.googas.bot.Guido;
import me.googas.commons.Validate;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.JsonClientThread;

/** Receptor for bungee to communicate with servers */
public class BungeeReceptors {

  @Receptor("bungee/is-online")
  public boolean isOnline(@ParamName("uuid") UUID uuid) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    if (bungee == null) return false;
    try {
      return Validate.notNullOr(
          bungee.sendRequest(
              new Request<>(Boolean.class, "bungee/is-online", Maps.singleton("uuid", uuid))),
          false);
    } catch (MessengerListenFailException e) {
      return false;
    }
  }
}
