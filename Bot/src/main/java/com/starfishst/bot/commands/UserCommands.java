package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.core.annotations.Optional;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.Collection;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;

/** Commands that users can use */
public class UserCommands {

  /**
   * Get the links form an user
   *
   * @param locale the locale that will read the links
   * @param sender the sender of the command
   * @param that the other user to see the links
   * @return the links of the user
   */
  @Command(aliases = "links", description = "links.desc", time = "5s")
  public Result links(
      LocaleFile locale,
      BotUser sender,
      @Optional(name = "links.user", description = "links.user.desc") BotUser that) {
    BotUser toSee;
    if (that != null) {
      toSee = that;
    } else {
      toSee = sender;
    }
    BotDataLoader loader = Guido.getDataLoader();
    Collection<BotLinkedData> links = loader.getLinks(toSee);
    if (links.isEmpty()) {
      return new Result(locale.get("links.empty", Maps.singleton("id", toSee.getId())));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("links.title", Maps.singleton("id", toSee.getId())));
      for (BotLinkedData link : links) {
        builder.append(link.getReadable(locale));
      }
      return new Result(builder.toString());
    }
  }
}

/*
public class UserCommands {

 The handler to localize the messages of the command
  @NotNull private final GuidoLanguageHandler handler = Guido.getLanguageHandler();

   * Manage users
   *
   * @param context the context of the command
   * @return the result saying to use a subcommand

  @Parent
  @Command(
      aliases = {"users", "user"},
      description = "cmd.users.desc",
      permission = @Perm(node = "guido.users"))
  public Result users(CommandContext context) {
    return new Result(handler.getFile(context).get("cmd.users.result"));
  }

   * Add a permission to an user
   *
   * @param commandContext the context of the command
   * @param user the user that is going to get the permission added
   * @param context the context of the command
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @return the result of the command execution
  @Command(
      aliases = {"add", "'addPerm"},
      description = "cmd.users.add.desc",
      permission = @Perm(node = "user:guido.users.add"))
  public Result add(
      CommandContext commandContext,
      @Required(
              name = "cmd.users.add.param.user.name",
              description = "cmd.users.add.param.user.desc")
          BotUser user,
      @Required(
              name = "cmd.users.add.param.context.name",
              description = "cmd.users.add.param.context.desc")
          String context,
      @Required(
              name = "cmd.users.add.param.node.name",
              description = "cmd.users.add.param.node.desc")
          String node,
      @Required(
              name = "cmd.users.add.param.enabled.name",
              description = "cmd.users.add.param.enabled.desc")
          boolean enabled) {
    user.addPermission(context, node, enabled);
    User discord = user.getDiscord();
    return new Result(
        handler
            .getFile(commandContext)
            .get(
                "cmd.users.add.result",
                Maps.builder("user", discord != null ? discord.getAsMention() : "Not reached")
                    .append("context", context)
                    .append("node", node)
                    .append("enabled", String.valueOf(enabled))));
  }

   * Removes the permission from an user
   *
   * @param commandContext the context of the command
   * @param user the user to remove the permission
   * @param context the context to remove the permission from
   * @param node the node of the permission
   * @return the result of the command if it was removed successfully
  @Command(
      aliases = {"remove", "remPerm"},
      description = "cmd.users.remove.desc",
      permission = @Perm(node = "user:guido.users.remove"))
  public Result remove(
      CommandContext commandContext,
      @Required(
              name = "cmd.users.remove.param.user.name",
              description = "cmd.users.remove.param.user.desc")
          BotUser user,
      @Required(
              name = "cmd.users.remove.param.context.name",
              description = "cmd.users.remove.param.context.desc")
          String context,
      @Required(
              name = "cmd.users.remove.param.node.name",
              description = "cmd.users.remove.param.node.desc")
          String node) {
    User discord = user.getDiscord();
    if (user.removePermission(context, node)) {
      return new Result(
          handler
              .getFile(commandContext)
              .get(
                  "cmd.users.remove.result.removed",
                  Maps.builder("user", discord != null ? discord.getAsMention() : "Not reached")
                      .append("context", context)
                      .append("node", node)));
    } else {
      return new Result(
          handler
              .getFile(commandContext)
              .get(
                  "cmd.users.remove.result.failed",
                  Maps.builder("user", discord != null ? discord.getAsMention() : "Not reached")
                      .append("context", context)
                      .append("node", node)));
    }
  }
}*/
