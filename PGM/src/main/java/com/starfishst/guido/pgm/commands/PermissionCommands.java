package com.starfishst.guido.pgm.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.pgm.api.Guido;
import com.starfishst.guido.pgm.api.commands.GuidoCommand;
import com.starfishst.guido.pgm.configuration.GuidoPermission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** Commands related to permissions */
public class PermissionCommands implements GuidoCommand {

  @Parent
  @Command(
      aliases = {"perm", "permission", "permissions"},
      description = "Manage permissions",
      permission = "guido.permissions")
  public Result perm() {
    return new Result("&eManage permissions using the following subcommands &agive&e, &aremove");
  }

  @Command(
      aliases = {"give", "add"},
      description = "Give a permission",
      permission = "guido.permissions.add")
  public Result give(
      @Required(name = "player", description = "The player to give the permission") Player player,
      @Required(name = "node", description = "The node of the permission") String node,
      @Required(name = "enabled", description = "Whether the permission is enabled")
          boolean enabled) {
    Guido.getData(player.getUniqueId()).addPermission(new GuidoPermission(node, enabled));
    return new Result("&eAdded the permission &a" + node + " &eto &a" + player.getName());
  }

  @Command(
      aliases = {"remove", "remove"},
      description = "Revokes a permission",
      permission = "guido.permissions.remove")
  public Result remove(
      @Required(name = "player", description = "The player to give the permission") Player player,
      @Required(name = "node", description = "The node of the permission") String node) {
    Guido.getData(player.getUniqueId()).removePermission(node);
    return new Result("&eRemoved the permission &a" + node + " &efrom &a" + player.getName());
  }

  @Override
  public void setEnabled(boolean bol) {
    // NOTHING
  }

  @Override
  public @NotNull String getName() {
    return "permission";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
