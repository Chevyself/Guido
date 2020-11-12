package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.client.requests.BungeeBooleanRequest;
import com.starfishst.bungee.core.client.requests.BungeeStringRequest;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import java.util.Map;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

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
  public void link(ProxiedPlayer player, BungeeLocaleFile locale) {
    ProxiedOfflinePlayer offline = new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
    Map<String, @NotNull LinkableInfo> params = Maps.singleton("info", offline.getLinkedInfo());
    new BungeeBooleanRequest("is-linked", params)
        .send(
            linked -> {
              if (!linked) {
                new BungeeStringRequest("link-code", params)
                    .send(
                        code -> {
                          if (code != null) {
                            player.sendMessage(
                                locale.getComponent("link.code", Maps.singleton("code", code)));
                          } else {
                            player.sendMessage(locale.getComponent("link.internal"));
                          }
                        });
              } else {
                player.sendMessage(locale.getComponent("link.linked"));
              }
            });
  }
}
