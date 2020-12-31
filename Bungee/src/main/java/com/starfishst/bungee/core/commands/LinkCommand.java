package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.client.requests.BungeeBooleanRequest;
import com.starfishst.bungee.core.client.requests.BungeeStringRequest;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.core.annotations.Settings;
import java.util.Map;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.maps.Maps;
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
  @Settings("async")
  @Command(aliases = "link")
  public void link(ProxiedPlayer player, BungeeLocaleFile locale) {
    ProxiedOfflinePlayer offline = new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
    Map<String, LinkableInfo> params = Maps.singleton("link", offline.getLinkedInfo());
    new BungeeBooleanRequest("link/is-linked", params)
        .send(
            linked -> {
              if (!linked) {
                new BungeeStringRequest("link-code", params)
                    .send(
                        code ->
                            player.sendMessage(
                                locale.getComponent("link.code", Maps.singleton("code", code))));
              } else {
                player.sendMessage(locale.getComponent("link.linked"));
              }
            });
  }
}
