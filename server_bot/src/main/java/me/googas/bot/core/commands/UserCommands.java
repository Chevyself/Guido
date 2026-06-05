package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.result.Result;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableMatcher;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.api.user.UserData;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.link.LinkHandler;

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
  @Command(aliases = "links", description = "links.desc")
  public Result links(
      LocaleFile locale,
      UserData sender,
      LinkableMatcher matcher,
      @Free(name = "links.user", description = "links.user.desc") UserData that) {
    UserData toSee;
    if (that != null) {
      toSee = that;
    } else {
      toSee = sender;
    }
    Collection<Linkable> links = matcher.getLinkedAccounts(toSee);
    Map<String, String> placeholders = Maps.singleton("id", toSee.getId().toString());
    if (links.isEmpty()) {
      return Result.of(locale.get("links.empty", placeholders));
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(locale.get("links.title", placeholders));
      for (Linkable link : links) {
        builder.append("\n - ").append(link.getPublicDisplayName(matcher));
      }
      return Result.of(builder.toString(), Duration.ofSeconds(5));
    }
  }

  /**
   * Link an user account with the given link code
   *
   * @param locale the locale of the command sender
   * @param user the user to link the account
   * @param code the code to getId the linked info
   * @return whether the link was successful
   */
  @Command(aliases = "link", description = "link.desc")
  public Result link(
      LocaleFile locale,
      UserData user,
      LinkableMatcher matcher,
      @Required(name = "link.code", description = "link.code.desc") String code) {
    Linkable link = Guido.getHandlers().getHandler(LinkHandler.class).getLinkable(code);
    if (link == null) {
      return Result.of(locale.get("link.code-not-match"));
    }
    link.setLinkedUser(user);
    return Result.of(
        locale.get("link.added", Maps.singleton("readable", link.getPublicDisplayName(matcher))));
  }

  @Command(aliases = "stats", description = "guido.stats.desc")
  public Result stats(
      CommandContext context,
      GuidoGuild guild,
      LocaleFile locale,
      UserData data,
      LinkableMatcher matcher,
      StatsProvider statsProvider,
      @Free(name = "stats.name", description = "stats.name.desc") String nickname) {
    Optional<MinecraftLinkable> optional = matcher.getMinecraftByLinkedUser(data.getId());
    return optional
        .map(
            linkable -> Result.of(this.buildStats(guild, statsProvider, locale, context, linkable)))
        .orElseGet(() -> Result.of(locale.get("stats.not-linked")));
  }

  private String buildStats(
      GuidoGuild guild,
      @NonNull StatsProvider statsProvider,
      @NonNull LocaleFile locale,
      @NonNull CommandContext context,
      @NonNull MinecraftLinkable linkable) {
    String nickname = linkable.getNickname();
    StringBuilder stringBuilder =
        new StringBuilder(locale.get(locale.get("stats.title", Maps.singleton("nick", nickname))));
    stringBuilder.append(locale.get("stats.desc", Maps.singleton("nick", nickname)));

    // query.setThumbnail("https://minotar.net/helm/" + nickname + "/100.png");

    // SortedStats organized = linkable.getOrganized(guild.getLadders());
    Stats stats = linkable.getStats(statsProvider);
    stats
        .getMap()
        .forEach(
            (key, value) -> {
              stringBuilder
                  .append("\n")
                  .append("- **")
                  .append(key)
                  .append("**: ")
                  .append("`")
                  .append(value)
                  .append("`");
            });
    return stringBuilder.toString();
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
    return new Result(handler.getFile(context).getId("cmd.users.result"));
  }

   * Add a permission to an user
   *
   * @param commandContext the context of the command
   * @param user the user that is going to getId the permission added
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
            .getId(
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
              .getId(
                  "cmd.users.remove.result.removed",
                  Maps.builder("user", discord != null ? discord.getAsMention() : "Not reached")
                      .append("context", context)
                      .append("node", node)));
    } else {
      return new Result(
          handler
              .getFile(commandContext)
              .getId(
                  "cmd.users.remove.result.failed",
                  Maps.builder("user", discord != null ? discord.getAsMention() : "Not reached")
                      .append("context", context)
                      .append("node", node)));
    }
  }
}*/
