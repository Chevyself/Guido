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

  @Command(aliases = "unlink")
  public Result unlink(MinecraftLink link) {
    if (link.getMinecraftUniqueId().isPresent()) {
      if (link.setMinecraftUniqueId(null)) {
        return Result.builder()
            .withMessageBuilder(builder -> builder.setContent("Your account has been unlinked"))
            .build();
      } else {
        return Result.forType(ResultType.ERROR)
            .withMessageBuilder(builder -> builder.setContent("Your account could not be unlinked"))
            .build();
      }
    }
    return Result.forType(ResultType.USAGE)
        .withMessageBuilder(builder -> builder.setContent("Your are not linked"))
        .build();
  }

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
            .withMessageBuilder(
                builder -> builder.setContent("You are already trying to link an account"))
            .build();
      }
      if (username == null) {
        return Result.forType(ResultType.USAGE)
            .withMessageBuilder(
                builder ->
                    builder.setContent("You must include the username of the account to link"))
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
            .withMessageBuilder(builder -> builder.setContent("Could not find player " + username))
            .build();
      }
      return null;
    } else {
      return Result.builder()
          .withMessageBuilder(
              builder ->
                  builder.setContent(
                      Strings.format(
                          "You are currently linked to the minecraft account {0}",
                          link.getMinecraftUniqueId().get())))
          .build();
    }
  }
}
