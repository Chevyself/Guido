package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.utils.BungeeUtils;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.Settings;
import java.util.ArrayList;
import me.googas.api.Requests;
import me.googas.api.client.data.permissions.SimplePermission;
import me.googas.api.permissions.Permission;
import me.googas.commons.Pagination;
import me.googas.commons.Strings;
import me.googas.commons.time.Time;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/** Commands for permission */
// TODO localization should shorten this class
public class PermissionCommands {

  @Settings("async")
  @Parent
  @Command(
      aliases = {"permissions", "perms", "perm"},
      permission = "guido.perms")
  public void perms(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The proxied player to add the permission to")
          ProxiedOfflinePlayer player,
      @Optional(
              name = "context",
              description = "The context to see the permissions",
              suggestions = "bungee")
          String context,
      @Optional(name = "page", description = "The page to see the permissions", suggestions = "1")
          int page,
      @Optional(
              name = "global",
              description = "Whether to see the permissions with the global permissions appended",
              suggestions = {"true", "false"})
          boolean global) {
    Requests.Links.permissions(player.getLinkedInfo(), context, global)
        .send(
            client,
            Requests.ifPresentElse(
                stack -> {
                  if (stack.getPermissions().isEmpty()) {
                    Pagination<Permission> pagination =
                        new Pagination<>(new ArrayList<>(stack.getPermissions()), 20);
                    int finalPage = page;
                    if (finalPage < 1 || page > pagination.maxPage()) {
                      finalPage = 1;
                    }
                    StringBuilder builder = Strings.getBuilder();
                    builder
                        .append("&7Permissions in &a")
                        .append(context)
                        .append("&7: &a")
                        .append(page)
                        .append("&8/")
                        .append(pagination.maxPage())
                        .append("\n");
                    for (Permission permission : pagination.getPage(finalPage)) {
                      builder.append("&8- ");
                      if (permission.isEnabled()) {
                        builder.append("&a");
                      } else {
                        builder.append("&c");
                      }
                      builder.append(permission.getNode()).append("\n");
                    }
                    sender.sendMessage(new TextComponent(BungeeUtils.build(builder.toString())));
                  } else {
                    sender.sendMessage(
                        new TextComponent(
                            BungeeUtils.build(
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

  @Settings("async")
  @Command(
      aliases = {"add", "give"},
      permission = "guido.perms.add")
  public void add(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The proxied player to add the permission to ")
          ProxiedOfflinePlayer player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Required(name = "enabled", description = "Whether the permission is enabled")
          boolean enabled,
      @Optional(
              name = "context",
              description = "The context to add the permission on",
              suggestions = "global")
          String context,
      @Optional(
              name = "expires",
              description = "When does the permission expire",
              suggestions = "0s")
          Time time) {
    Requests.Links.permission(
            player.getLinkedInfo(),
            context,
            new SimplePermission(
                node,
                enabled,
                time.millis() == 0 ? -1 : System.currentTimeMillis() + time.millis()))
        .send(
            client,
            Requests.ifPresentElse(
                added -> {
                  TextComponent message;
                  if (added) {
                    message =
                        new TextComponent(
                            BungeeUtils.build(
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
                        new TextComponent(BungeeUtils.build("&cPermission could not be given"));
                  }
                  sender.sendMessage(message);
                },
                () -> {
                  // TODO could not be added
                }));
  }

  @Settings("async")
  @Command(
      aliases = {"remove", "revoke"},
      permission = "guido.perms.remove")
  public void add(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The proxied player to revoke the permission from ")
          ProxiedOfflinePlayer player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Optional(
              name = "context",
              description = "The context to add the permission on",
              suggestions = "bungee")
          String context) {
    Requests.Links.removePermission(player.getLinkedInfo(), context, node)
        .send(
            client,
            Requests.ifPresentElse(
                removed -> {
                  TextComponent message;
                  if (removed) {
                    message =
                        new TextComponent(
                            BungeeUtils.build(
                                "&aPermission &e"
                                    + node
                                    + "&a from the context &e"
                                    + context
                                    + "&a has been removed from &e"
                                    + player.getNickname()));
                  } else {
                    message =
                        new TextComponent(BungeeUtils.build("&cPermission could not be revoked"));
                  }
                  sender.sendMessage(message);
                },
                () -> {
                  // TODO could not be removed
                }));
  }
}
