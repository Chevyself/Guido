package me.googas.bot.core.commands;

import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import me.googas.bot.core.Guido;
import me.googas.messaging.Request;
import me.googas.messaging.api.Messenger;

public class TestCommands {

  @Command(aliases = "test", permission = @Perm(node = "user:guido.test"))
  public void test() {
    for (Messenger client : Guido.getServer().getClients()) {
      client.sendRequest(
          new Request<>(Boolean.class, "asd"),
          bol -> {
            System.out.println(bol);
          });
    }
  }
}
