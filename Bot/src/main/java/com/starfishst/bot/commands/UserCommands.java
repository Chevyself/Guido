package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.handlers.link.LinkHandler;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
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
      UserData sender,
      @Optional(name = "links.user", description = "links.user.desc") UserData that) {
    UserData toSee;
    if (that != null) {
      toSee = that;
    } else {
      toSee = sender;
    }
    BotDataLoader loader = Guido.getDataLoader();
    Collection<LinkedData> links = loader.getLinks(toSee);
    if (links.isEmpty()) {
      return new Result(locale.get("links.empty", Maps.singleton("id", toSee.getId())));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("links.title", Maps.singleton("id", toSee.getId())));
      for (LinkedData link : links) {
        builder.append(link.getReadable(locale));
      }
      return new Result(builder.toString());
    }
  }

  /**
   * Link an user account with the given link code
   *
   * @param locale the locale of the command sender
   * @param user the user to link the account
   * @param code the code to get the linked info
   * @return whether the link was successful
   */
  @Command(aliases = "link", description = "link.desc")
  public Result link(
      LocaleFile locale,
      UserData user,
      @Required(name = "link.code", description = "link.code.desc") String code) {
    LinkedInfo info = Guido.getHandler(LinkHandler.class).getInfo(code);
    if (info != null) {
      Collection<LinkedData> links = Guido.getDataLoader().getLinks(user, info.getType());
      if (links.size() >= 1) {
        return new Result(
            ResultType.ERROR,
            locale.get(
                "link.only-one", Maps.singleton("type", info.getType().toString().toLowerCase())));
      } else {
        BotLinkedData data =
            Guido.getDataLoader().getLinkedData(info.getType(), info.getIdentification(), true);
        if (data != null) {
          data.setLinkedUser(user);
          return new Result(
              locale.get("link.added", Maps.singleton("readable", data.getReadable(locale))));
        } else {
          return new Result(ResultType.UNKNOWN, locale.get("link.data-not-found"));
        }
      }
    } else {
      return new Result(ResultType.USAGE, locale.get("link.code-not-match"));
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
