package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.core.annotations.Settings;
import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands for linking minecraft accounts */
public class LinkCommand {

  @Settings("async")
  @Command(aliases = "link")
  public void link(ProxiedPlayer player, BungeeLocaleFile locale, JsonClient client) {
    // TODO make proxied offline player as an extra argument
    ProxiedOfflinePlayer offline = new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
    LinkableInfo link = offline.getLinkedInfo();
    Requests.Links.isLinked(link)
        .send(
            client,
            Requests.ifPresentElse(
                linked -> {
                  if (!linked) {
                    Request.builder(String.class, "link-code")
                        .put("link", link)
                        .send(
                            client,
                            Requests.ifPresentElse(
                                code -> {
                                  player.sendMessage(
                                      locale.getComponent(
                                          "link.code", Maps.singleton("code", code)));
                                },
                                () -> {
                                  // TODO could not retrieve code
                                }));
                  } else {
                    player.sendMessage(locale.getComponent("link.linked"));
                  }
                },
                () -> {
                  // TODO could not check if is linked
                }));
  }
}
