package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.result.Result;
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
    return Result.of(locale.get("lobby.success"));
  }

  // TODO create /servers and /server

}
