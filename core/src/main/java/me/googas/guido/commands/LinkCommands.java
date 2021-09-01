package me.googas.guido.commands;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import me.googas.commands.annotations.Free;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import me.googas.guido.GuidoBot;
import me.googas.guido.receptors.LinkReceptors;
import me.googas.guido.type.MinecraftLink;
import me.googas.net.api.messages.RequestBuilder;
import me.googas.starbox.Strings;

public class LinkCommands {

  @Command(aliases = "link")
  public Result link(
      MinecraftLink link,
      @Free(name = "username", description = "The minecraft username") String username) {
    if (!link.getMinecraftUniqueId().isPresent()) {
      if (GuidoBot.getCache()
          .get(
              LinkReceptors.LinkCode.class,
              linkCode -> linkCode.getUser() == link.getUser().getIdLong())
          .isPresent()) {
        return Result.forType(ResultType.USAGE)
            .setDescription("You are already trying to link an account")
            .build();
      }
      if (username == null) {
        return Result.forType(ResultType.USAGE)
            .setDescription("You must include the username of the account to link")
            .build();
      }
      AtomicBoolean notified = new AtomicBoolean();
      int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
      GuidoBot.getServer()
          .sendRequest(
              new RequestBuilder<>(UUID.class, "link/notify")
                  .put("username", username)
                  .put("code", code)
                  .put("tag", link.getUser().getAsTag())
                  .build())
          .forEach(
              (client, response) -> {
                if (response.isPresent()) {
                  notified.set(true);
                  GuidoBot.getCache()
                      .add(
                          new LinkReceptors.LinkCode(
                              code, link.getUser().getIdLong(), response.get()));
                }
              });
      if (!notified.get()) {
        return Result.forType(ResultType.ERROR)
            .setDescription("The player " + username + " is not online")
            .build();
      } else {
        return Result.builder()
            .setDescription("A notification to accept has been sent to the player")
            .build();
      }
    } else {
      return Result.builder()
          .setDescription(
              Strings.format(
                  "You are currently linked to the Minecraft account {0}",
                  link.getMinecraftUniqueId().get()))
          .build();
    }
  }
}
