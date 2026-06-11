package dev.xevy.guido.mc;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.common.Async;
import com.github.chevyself.starbox.result.Result;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.utility.Maps;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.logging.LoggerFactory;

public class LinkCommand {
  @NonNull private static final Logger logger = LoggerFactory.getLogger(LinkCommand.class);

  @Async
  @Command(aliases = "link")
  public Result link(
      MinecraftPlayer player, JsonClient client, MinecraftResultProvider resultProvider) {
    try {
      boolean linked = Requests.MinecraftLinks.isLinked(player.getUniqueId()).future(client).get();
      if (linked) {
        return resultProvider.getResult(player, "link.linked");
      }
      String code = Requests.MinecraftLinks.linkNew(player.getUniqueId()).future(client).get();
      if (code == null || code.isBlank()) {
        return resultProvider.getResult(player, "link.failed");
      }
      return resultProvider.getResult(player, "link.code", Maps.singleton("code", code));
    } catch (ExecutionException | InterruptedException e) {
      logger.log(Level.SEVERE, "Failed to run link command", e);
      return resultProvider.getResult(player, "link.failede");
    }
  }
}
