package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.bungee.utils.BungeeUtils;
import com.github.chevyself.starbox.common.Async;
import com.github.chevyself.starbox.common.CommandPermission;
import java.util.ArrayList;
import me.googas.api.Requests;
import me.googas.api.permissions.AbstractPermission;
import me.googas.bungee.data.ProxiedOfflinePlayer;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.Pagination;
import me.googas.starbox.time.Time;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/** Commands for permission */
// TODO localization should shorten this class
public class PermissionCommands {

  @Async
  @Parent
  @CommandPermission("guido.perms")
  @Command(aliases = {"permissions", "perms", "perm"})
  public void perms(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The proxied player to add the permission to")
          ProxiedOfflinePlayer player,
      @Free(
              name = "context",
              description = "The context to see the permissions",
              suggestions = "bungee")
          String context,
      @Free(name = "page", description = "The page to see the permissions", suggestions = "1")
          int page,
      @Free(
              name = "global",
              description = "Whether to see the permissions with the global permissions appended",
              suggestions = {"true", "false"})
          boolean global) {
    Requests.Links.permissions(player.getLink(), context, global)
        .send(
            client,
            Requests.ifPresentElse(
                stack -> {
                  if (!stack.getPermissions().isEmpty()) {
                    Pagination<AbstractPermission> pagination =
                        new Pagination<>(new ArrayList<>(stack.getPermissions()), 20);
                    int finalPage = page;
                    if (finalPage < 1 || page > pagination.maxPage()) {
                      finalPage = 1;
                    }
                    StringBuilder builder = new StringBuilder();
                    builder
                        .append("&7Permissions in &a")
                        .append(context)
                        .append("&7: &a")
                        .append(page)
                        .append("&8/")
                        .append(pagination.maxPage())
                        .append("\n");
                    for (AbstractPermission abstractPermission : pagination.getPage(finalPage)) {
                      builder.append("&8- ");
                      if (abstractPermission.isEnabled()) {
                        builder.append("&a");
                      } else {
                        builder.append("&c");
                      }
                      builder.append(abstractPermission.getNode()).append("\n");
                    }
                    sender.sendMessage(new TextComponent(BungeeUtils.format(builder.toString())));
                  } else {
                    sender.sendMessage(
                        new TextComponent(
                            BungeeUtils.format(
                                "&e"
                                    + player.getNickname()
                                    + "&c permissions in context: &e"
                                    + context
                                    + "&c is empty")));
                  }
                },
                () -> {
                  // TODO stack could not be retrieved
                }));
  }

  @Async
  @CommandPermission("guido.perms.add")
  @Command(aliases = {"add", "give"})
  public void add(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The proxied player to add the permission to ")
          ProxiedOfflinePlayer player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Required(name = "enabled", description = "Whether the permission is enabled")
          boolean enabled,
      @Free(
              name = "context",
              description = "The context to add the permission on",
              suggestions = "global")
          String context,
      @Free(
              name = "getExpires",
              description = "When does the permission expire",
              suggestions = "0s")
          Time time) {
    Requests.Links.permission(
            player.getLink(),
            context,
            new AbstractPermission(
                node,
                enabled,
                time.toMillisRound() == 0 ? -1 : System.currentTimeMillis() + time.toMillisRound()))
        .send(
            client,
            Requests.ifPresentElse(
                added -> {
                  TextComponent message;
                  if (added) {
                    message =
                        new TextComponent(
                            BungeeUtils.format(
                                "&aPermission &e"
                                    + node
                                    + "&a in the status &e"
                                    + enabled
                                    + "&a in the context &e"
                                    + context
                                    + "&a has been given to &e"
                                    + player.getNickname()));
                  } else {
                    message =
                        new TextComponent(BungeeUtils.format("&cPermission could not be given"));
                  }
                  sender.sendMessage(message);
                },
                () -> {
                  // TODO could not be added
                }));
  }

  @Async
  @CommandPermission("guido.perms.remove")
  @Command(aliases = {"remove", "revoke"})
  public void add(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The proxied player to revoke the permission from ")
          ProxiedOfflinePlayer player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Free(
              name = "context",
              description = "The context to add the permission on",
              suggestions = "bungee")
          String context) {
    Requests.Links.removePermission(player.getLink(), context, node)
        .send(
            client,
            Requests.ifPresentElse(
                removed -> {
                  TextComponent message;
                  if (removed) {
                    message =
                        new TextComponent(
                            BungeeUtils.format(
                                "&aPermission &e"
                                    + node
                                    + "&a from the context &e"
                                    + context
                                    + "&a has been removed from &e"
                                    + player.getNickname()));
                  } else {
                    message =
                        new TextComponent(BungeeUtils.format("&cPermission could not be revoked"));
                  }
                  sender.sendMessage(message);
                },
                () -> {
                  // TODO could not be removed
                }));
  }
}
