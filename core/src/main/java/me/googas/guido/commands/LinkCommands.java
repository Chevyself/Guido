package me.googas.guido.commands;

import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.guido.type.MinecraftLink;
import me.googas.starbox.Strings;

public class LinkCommands {

  @Command(aliases = "link")
  public Result link(MinecraftLink link) {
    if (!link.getMinecraftUniqueId().isPresent()) {
      return Result.builder()
          .withMessageBuilder(builder -> builder.setContent("You are not currently linked"))
          .build();
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
