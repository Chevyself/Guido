package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.utils.BungeeUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.NonNull;
import me.googas.commons.CoreFiles;
import me.googas.commons.RandomUtils;
import me.googas.commons.fallback.Fallback;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.event.EventHandler;

/** Changes the motd when a player pings the server */
public class MotdListener implements GuidoListener {

  /** The favicon that will be the response on a server ping */
  private final Favicon favicon;

  /** Create the motd listener */
  public MotdListener() {
    this.favicon = this.readFavicon();
  }

  /**
   * Read the favicon image that the listener must use. The image has to be placed in the same
   * folder as the other server files with the name 'server.png'
   *
   * @return the read favicon or null if the file could not be read
   */
  private Favicon readFavicon() {
    File file = CoreFiles.getFile(CoreFiles.currentDirectory(), "server.png");
    if (file != null) {
      try {
        return Favicon.create(ImageIO.read(file));
      } catch (IOException e) {
        Fallback.addError("IOException: server.png could not be read");
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * When the proxy is pinged receive the players with the custom server moth
   *
   * @param event the event of a proxy
   */
  @EventHandler(priority = 5)
  public void onProxyPing(ProxyPingEvent event) {
    event.setResponse(this.getPing(event));
  }

  /**
   * Get the server ping that will be sent in the event
   *
   * @param event the event of a player getting a server ping
   * @return the server ping
   */
  @NonNull
  public ServerPing getPing(ProxyPingEvent event) {
    ServerPing.Protocol version = event.getResponse().getVersion();
    int online = event.getResponse().getPlayers().getOnline();
    return new ServerPing(
        version,
        new ServerPing.Players(10000, online, this.getPlayerInfo()),
        this.getDescription(),
        this.favicon);
  }

  /**
   * Get the description that will be send in the server motd
   *
   * @return the description to send to the client
   */
  @NonNull
  public BaseComponent getDescription() {
    return BungeeUtils.getComponent(
        BungeeUtils.build(
            RandomUtils.getRandom(this.getSettings().getListSetting("descriptions"))))[0];
  }

  /**
   * Get the list of players names to send to the client
   *
   * @return the list of players names
   */
  @NonNull
  public List<String> getPlayers() {
    return RandomUtils.getRandom(this.getSettings().getListSetting("players"));
  }

  /**
   * Get the player info to send in a server ping
   *
   * @return the player info to send
   */
  @NonNull
  private ServerPing.PlayerInfo[] getPlayerInfo() {
    List<String> players = this.getPlayers();
    ServerPing.PlayerInfo[] info = new ServerPing.PlayerInfo[players.size()];
    for (int i = 0; i < players.size(); i++) {
      info[i] = new ServerPing.PlayerInfo(BungeeUtils.build(players.get(i)), UUID.randomUUID());
    }
    return info;
  }

  @Override
  public @NonNull String getName() {
    return "motd";
  }

  @Override
  public void onUnload() {}
}
