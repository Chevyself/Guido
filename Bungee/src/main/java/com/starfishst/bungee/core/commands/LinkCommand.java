package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.result.Result;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import me.googas.api.client.Client;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands for linking minecraft accounts */
public class LinkCommand {

  /**
   * Get a link code
   *
   * @param player the player to get a link code
   * @param locale the locale of the sender
   * @return an empty result everything is async
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(aliases = "link")
  public Result link(ProxiedPlayer player, BungeeLocaleFile locale)
      throws MessengerListenFailException {
    ProxiedOfflinePlayer offline = new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
    Client client = Guido.getClient();
    client.request(
        new Request<>(Boolean.class, "is-linked", Maps.singleton("info", offline.getLinkedInfo())),
        linked -> {
          if (!linked) {
            try {
              client.request(
                  new Request<>(
                      String.class, "link-code", Maps.singleton("info", offline.getLinkedInfo())),
                  code -> {
                    if (code != null) {
                      player.sendMessage(
                          locale.getComponent("link.code", Maps.singleton("code", code)));
                    } else {
                      player.sendMessage(locale.getComponent("link.internal"));
                    }
                  });
            } catch (MessengerListenFailException e) {
              e.printStackTrace();
            }
          } else {
            player.sendMessage(locale.getComponent("link.linked"));
          }
        });
    return new Result();
  }
}
