package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.core.client.requests.BungeeBooleanRequest;
import com.starfishst.bungee.core.client.requests.BungeeRequest;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.utils.BungeeUtils;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import java.util.ArrayList;
import me.googas.api.client.data.PermissionImpl;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.Pagination;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/** Commands for permission */
public class PermissionCommands {

  /**
   * Get the permissions from a player
   *
   * @param sender the sender of the command
   * @param player the player to see the permission
   * @param context the context to get the permissions on
   * @param page the page to see
   * @return the permissions
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Parent
  @Command(
      aliases = {"permissions", "perms", "perm"},
      permission = "guido.perms")
  public void perms(
      CommandSender sender,
      @Required(name = "player", description = "The proxied player to add the permission to ")
          ProxiedOfflinePlayer player,
      @Optional(
              name = "context",
              description = "The context to see the permissions",
              suggestions = "bungee")
          String context,
      @Optional(name = "page", description = "The page to see the permissions", suggestions = "1")
          int page) {
    new BungeeRequest<>(
            PermissionStack.class,
            "permission",
            Maps.objects("info", player.getLinkedInfo()).append("context", context).build())
        .send(
            stack -> {
              if (stack != null && !stack.getPermissions().isEmpty()) {
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
            });
  }

  /**
   * Give a permission to a player
   *
   * @param sender the sender of the command
   * @param player the player getting the permission
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @param context the context which the permission will be given in
   * @return an empty result
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"add", "give"},
      permission = "guido.perms.add")
  public void add(
      CommandSender sender,
      @Required(name = "player", description = "The proxied player to add the permission to ")
          ProxiedOfflinePlayer player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Required(name = "enabled", description = "Whether the permission is enabled")
          boolean enabled,
      @Optional(
              name = "context",
              description = "The context to add the permission on",
              suggestions = "bungee")
          String context) {

    new BungeeBooleanRequest(
            "add-permission",
            Maps.objects("info", player.getLinkedInfo())
                .append("context", context)
                .append("permission", new PermissionImpl(node, enabled).getNodeAppended())
                .build())
        .send(
            bol -> {
              TextComponent message;
              if (bol) {
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
                message = new TextComponent(BungeeUtils.build("&cPermission could not be given"));
              }
              sender.sendMessage(message);
            });
  }

  /**
   * Remove a permission from a player
   *
   * @param sender the sender of the command
   * @param player the player getting the permission removed
   * @param node the node of the permission
   * @param context the context which the permission will be removed from
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"remove", "revoke"},
      permission = "guido.perms.remove")
  public void add(
      CommandSender sender,
      @Required(name = "player", description = "The proxied player to revoke the permission from ")
          ProxiedOfflinePlayer player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Optional(
              name = "context",
              description = "The context to add the permission on",
              suggestions = "bungee")
          String context) {

    new BungeeBooleanRequest(
            "remove-permission",
            Maps.objects("info", player.getLinkedInfo())
                .append("context", context)
                .append("permission", new PermissionImpl(node, true).getNodeAppended())
                .build())
        .send(
            bol -> {
              TextComponent message;
              if (bol) {
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
                message = new TextComponent(BungeeUtils.build("&cPermission could not be revoked"));
              }
              sender.sendMessage(message);
            },
            exception -> {
              exception.printStackTrace();
              sender.sendMessage(
                  new TextComponent(BungeeUtils.build("&cPermission could not be revoked")));
            });
  }
}
