package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import com.starfishst.guido.api.data.implementations.data.PermissionImpl;
import com.starfishst.guido.api.data.implementations.data.PermissionStackImpl;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.ArrayList;
import java.util.HashMap;
import me.googas.commons.Pagination;
import me.googas.commons.Strings;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;

/** Commands for permission */
public class PermissionCommands {

  /**
   * Get the permissions from a player
   *
   * @param player the player to see the permission
   * @param page the page to see
   * @return the permissions
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Parent
  @Command(
      aliases = {"permissions", "perms", "perm"},
      permission = "guido.perms")
  public Result perms(
      @Required(name = "player", description = "The proxied player to add the permission to ")
          ProxiedOfflinePlayer player,
      @Optional(
              name = "context",
              description = "The context to see the permissions",
              suggestions = "")
          String context,
      @Optional(name = "page", description = "The page to see the permissions", suggestions = "1")
          int page) {
    PermissionStackImpl stack =
        Guido.getClient()
            .request(
                new Request<>(
                    PermissionStackImpl.class,
                    "permission",
                    Maps.objects("type", LinkedDataType.MINECRAFT)
                        .append(
                            "identification", Maps.objects("uuid", player.getUniqueId()).build())
                        .append("context", context)
                        .build()));
    if (stack != null && !stack.getPermissions().isEmpty()) {
      Pagination<PermissionImpl> pagination =
          new Pagination<>(new ArrayList<>(stack.getPermissions()), 20);
      if (page < 1 || page > pagination.maxPage()) {
        page = 1;
      }
      StringBuilder builder = Strings.getBuilder();
      builder
          .append("&7Permissions: &a")
          .append(page)
          .append("&8/")
          .append(pagination.maxPage())
          .append("\n");
      for (PermissionImpl permission : pagination.getPage(page)) {
        builder.append("&8- ");
        if (permission.isEnabled()) {
          builder.append("&a");
        } else {
          builder.append("&c");
        }
        builder.append(permission.getNode()).append("\n");
      }
      return new Result(builder.toString());
    } else {
      return new Result("&cEmpty");
    }
  }

  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"add", "give"},
      permission = "guido.perms.add")
  public Result add(
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
    HashMap<String, Object> identification =
        Maps.objects("uuid", UUIDUtils.trim(player.getUniqueId())).build();
    HashMap<String, Object> params =
        Maps.objects("type", LinkedDataType.MINECRAFT)
            .append("context", context)
            .append("permission", new PermissionImpl(node, enabled).getNodeAppended())
            .append("identification", identification)
            .build();
    Boolean added =
        Guido.getClient().request(new Request<>(Boolean.class, "add-permission", params));
    if (added) {
      return new Result("Permission added");
    } else {
      return new Result("Permission was not added");
    }
  }
}
