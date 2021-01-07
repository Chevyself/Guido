package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import com.starfishst.jda.utils.embeds.EmbedFactory;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.user.UserData;
import me.googas.api.utility.SortedStats;
import me.googas.bot.api.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.handlers.link.LinkHandler;
import me.googas.bot.core.loader.GuidoLoader;
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
    GuidoLoader loader = Guido.getHandlers().getLoader();
    Collection<Linkable> links = loader.getLinks().getLinks(toSee);
    if (links.isEmpty()) {
      return new Result(locale.get("links.empty", Maps.singleton("id", toSee.getId())));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("links.title", Maps.singleton("id", toSee.getId())));
      for (Linkable link : links) {
        builder.append("\n - ").append(link.getSingle());
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
    GuidoLoader loader = Guido.getHandlers().getLoader();
    LinkableInfo info = Guido.getHandlers().getHandler(LinkHandler.class).getInfo(code);
    if (info != null) {
      Linkable link = loader.getLinks().getLink(user, info.getType());
      if (link != null) {
        return new Result(
            ResultType.ERROR,
            locale.get(
                "link.only-one", Maps.singleton("type", info.getType().toString().toLowerCase())));
      } else {
        Linkable data = loader.getLinks().getLink(info.getType(), info.getIdentification());
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

  @Command(aliases = "stats", description = "guido.stats.desc")
  public Result stats(
      CommandContext context,
      BotGuild guild,
      LocaleFile locale,
      UserData data,
      @Optional(name = "stats.name", description = "stats.name.desc") String nickname) {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    Linkable link = loader.getLinks().getLink(data, LinkableType.MINECRAFT);
    if (nickname != null) {
      link =
          loader
              .getLinks()
              .getLinkByRecognition(
                  LinkableType.MINECRAFT, new GuidoValuesMap("nickname", nickname));
      if (link != null) {
        return new Result(this.buildStats(guild, locale, context, link));
      } else {
        return new Result(locale.get("stats.player-not-found", Maps.singleton("nick", nickname)));
      }
    } else {
      if (link != null) {
        return new Result(this.buildStats(guild, locale, context, link));
      } else {
        return new Result(locale.get("stats.not-linked"));
      }
    }
  }

  private EmbedQuery buildStats(
      BotGuild guild,
      @NonNull LocaleFile locale,
      @NonNull CommandContext context,
      @NonNull Linkable linkable) {
    String nickname = linkable.getRecognition().getOr("nickname", String.class, "");
    EmbedQuery query =
        EmbedFactory.fromResult(
            new Result(locale.get("stats.desc", Maps.singleton("nick", nickname))),
            Guido.getCommandManager().getListener(),
            context);
    query.setTitle(locale.get(locale.get("stats.title", Maps.singleton("nick", nickname))));
    query.setThumbnail("https://minotar.net/helm/" + nickname + "/100.png");
    SortedStats organized = linkable.getOrganized(guild.getLadders());
    organized
        .getMap()
        .forEach(
            (statContext, stats) -> {
              StringBuilder builder = Strings.getBuilder();
              stats.forEach(
                  (stat, value) ->
                      builder
                          .append("\n")
                          .append("- **")
                          .append(stat)
                          .append("**: ")
                          .append("`")
                          .append(value)
                          .append("`"));
              query.addField(statContext.replace("_", " "), builder.toString(), true);
            });
    return query;
  }
}

/*
public class UserCommands {

 The handler to localize the messages of the command
  @NotNull private final GuidoLanguageHandler handler = GuidoBot.getLanguageHandler();

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
    User discord = user.toDiscord();
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
    User discord = user.toDiscord();
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
