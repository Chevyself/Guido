package me.googas.bungee.commands;

import com.starfishst.commands.bungee.annotations.Command;
import com.starfishst.commands.bungee.result.Result;
import me.googas.api.lang.LocaleFile;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands for joining the lobby */
public class ServerCommands {

  @Command(aliases = {"lobby", "hub"})
  public Result lobby(LocaleFile locale, ProxiedPlayer player) {
    ServerInfo lobby = ProxyServer.getInstance().getServerInfo("lobby");
    player.connect(lobby);
    return new Result(locale.get("lobby.success"));
  }

  // TODO create /servers and /server

}
