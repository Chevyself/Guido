package me.googas.guido.commands;

import java.util.Optional;
import me.googas.commands.annotations.Free;
import me.googas.commands.bukkit.annotations.Command;
import me.googas.commands.bukkit.result.Result;
import me.googas.guido.Guido;
import me.googas.net.api.messages.RequestBuilder;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.BukkitLine;
import me.googas.starbox.modules.channels.Channel;

public class LinkCommands {

  @Command(aliases = "link", description = "Link your account to discord", async = true)
  public Result link(
      Channel channel,
      @Free(name = "code", description = "The code to link your account with") int code) {
    Optional<JsonClient> socket = Guido.getClient().getSocket();
    if (socket.isPresent()) {
      socket
          .get()
          .sendRequest(
              new RequestBuilder<>(Boolean.class, "links/link").put("code", code).build(),
              (response) -> {
                if (response.isPresent() && response.get()) {
                  channel.send(BukkitLine.of("&aYour account has been linked").build());
                } else {
                  channel.send(
                      BukkitLine.of("&cYour account could not be linked. Code might be wrong")
                          .build());
                }
              });
      return new Result();
    } else {
      return BukkitLine.of("&cYou cannot link your account at this time").asResult();
    }
  }
}
