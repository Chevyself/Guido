package me.googas.api.server.receptors;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.commons.Validate;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.JsonClientThread;

/** Receptors redirected to the bungee client */
public class BungeeRedirect {

  @NonNull private final GuidoAuthenticator authenticator;

  public BungeeRedirect(@NonNull GuidoAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  @Receptor(Requests.Bungee.IS_ONLINE)
  public boolean isOnline(@ParamName("uuid") UUID uuid) {
    JsonClientThread bungee = this.authenticator.getBungee();
    if (bungee == null) return false;
    try {
      return Validate.notNullOr(Requests.Bungee.isOnline(uuid).send(bungee), false);
    } catch (MessengerListenFailException e) {
      return false;
    }
  }
}
