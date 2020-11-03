package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.core.client.requests.BungeeBooleanRequest;
import com.starfishst.bungee.core.client.requests.BungeeStringRequest;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.core.listeners.GroupListener;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import me.googas.api.client.data.PermissionImpl;
import me.googas.api.permissions.Group;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.CommandSender;

/** Commands for groups */
public class GroupCommands {

  /**
   * See all the loaded groups
   *
   * @return the loaded groups
   */
  @Parent
  @Command(
      aliases = {"groups", "group"},
      permission = "guido.groups")
  public void groups() {}

  /**
   * Create a new group
   *
   * @param locale the locale of the command sender
   * @param sender the sender of the command
   * @param name the name of the new group
   * @param weight the weight of the new group
   * @return empty result
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"create", "make"},
      permission = "guido.groups.create")
  public void create(
      BungeeLocaleFile locale,
      CommandSender sender,
      @Required(name = "create.name", description = "create.name.desc") String name,
      @Required(name = "create.weight", description = "create.weight.desc") int weight) {
    new BungeeStringRequest("create-group", Maps.objects("name", name).append("weight", weight))
        .send(
            string -> {
              if (string != null) {
                sender.sendMessage(
                    locale.getComponent("groups.create.success", Maps.singleton("id", string)));
              } else {
                sender.sendMessage(locale.getComponent("groups.create.fail"));
              }
            },
            exception -> {
              exception.printStackTrace();
              sender.sendMessage(locale.getComponent("groups.create.fail"));
            });
  }

  /**
   * Add a permission to a group
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @param group the group to give the permission
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @param context the context to give the permission on
   * @return empty result
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"add", "give"},
      permission = "guido.groups.add")
  public void add(
      BungeeLocaleFile locale,
      CommandSender sender,
      @Required(name = "add.group", description = "add.group.desc") Group group,
      @Required(name = "add.node", description = "add.node.desc") String node,
      @Required(name = "add.enabled", description = "add.enabled.desc") boolean enabled,
      @Optional(name = "add.context", description = "add.context.desc", suggestions = "bungee")
          String context) {
    new BungeeBooleanRequest(
            "group-add-permission",
            Maps.builder("id", group.getId())
                .append("context", context)
                .append("permission", new PermissionImpl(node, enabled).getNodeAppended()))
        .send(
            added -> {
              if (added) {
                sender.sendMessage(
                    locale.getComponent(
                        "groups.add.success",
                        Maps.builder("name", group.getName())
                            .append("node", node)
                            .append("enabled", String.valueOf(enabled))
                            .append("context", context)));
              } else {
                sender.sendMessage(
                    locale.getComponent(
                        "groups.add.fail",
                        Maps.builder("name", group.getName())
                            .append("node", node)
                            .append("enabled", String.valueOf(enabled))
                            .append("context", context)));
              }
            },
            exception -> {
              sender.sendMessage(
                  locale.getComponent(
                      "groups.add.fail",
                      Maps.builder("name", group.getName())
                          .append("node", node)
                          .append("enabled", String.valueOf(enabled))
                          .append("context", context)));
            });
  }

  /**
   * Revoke a permission from a group
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @param group the group to revoke the permission
   * @param node the node of the permission to remove
   * @param context the context to remove the permission on
   * @return empty result
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"remove", "revoke"},
      permission = "guido.groups.remove")
  public void remove(
      BungeeLocaleFile locale,
      CommandSender sender,
      @Required(name = "remove.group", description = "remove.group.desc") Group group,
      @Required(name = "remove.node", description = "remove.node.desc") String node,
      @Optional(
              name = "remove.context",
              description = "remove.context.desc",
              suggestions = "bungee")
          String context) {
    new BungeeBooleanRequest(
            "group-remove-permission",
            Maps.objects("id", group.getId())
                .append("context", context)
                .append("permission", new PermissionImpl(node, false)))
        .send(
            removed -> {
              if (removed) {
                sender.sendMessage(
                    locale.getComponent(
                        "groups.remove.success",
                        Maps.builder("name", group.getName())
                            .append("node", node)
                            .append("context", context)));
              } else {
                sender.sendMessage(
                    locale.getComponent(
                        "groups.remove.fail",
                        Maps.builder("name", group.getName())
                            .append("node", node)
                            .append("context", context)));
              }
            },
            exception -> {
              sender.sendMessage(
                  locale.getComponent(
                      "groups.remove.fail",
                      Maps.builder("name", group.getName())
                          .append("node", node)
                          .append("context", context)));
            });
  }

  /**
   * Reload the loaded groups
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @return empty result
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"reload"},
      permission = "guido.groups.reload")
  public void reload(BungeeLocaleFile locale, CommandSender sender) {
    Guido.getListener(GroupListener.class)
        .reload(
            loaded -> {
              if (loaded != null) {
                sender.sendMessage(
                    locale.getComponent(
                        "groups.reload", Maps.singleton("amount", String.valueOf(loaded.size()))));
              }
            });
  }
}
