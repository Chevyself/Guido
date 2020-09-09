package com.starfishst.guido.pgm.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** Commands used for flight */
public class FlyCommand implements GuidoCommand {

  /** Whether the command is enabled */
  private boolean enabled = false;

  /**
   * Toggle the fly status for a player or another
   *
   * @param player the player sender of the command
   * @param optional the optional player to toggle fly
   * @return the result of the command execution
   */
  @Command(aliases = "fly", description = "Toggle your fly status", permission = "guido.fly")
  public Result fly(
      Player player,
      @Optional(name = "player", description = "The player to toggle fly to") Player optional) {
    if (optional != null && optional != player) {
      if (optional.getAllowFlight()) {
        optional.setAllowFlight(false);
        return new Result("&eFly for " + optional.getName() + "  has been disabled");
      } else {
        optional.setAllowFlight(true);
        return new Result("&eFly for " + optional.getName() + "  has been enabled");
      }
    } else {
      if (player.getAllowFlight()) {
        player.setAllowFlight(false);
        return new Result("&eFly has been disabled");
      } else {
        player.setAllowFlight(true);
        return new Result("&eFly has been enabled");
      }
    }
  }

  @Override
  public @NotNull String getName() {
    return "fly";
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
  }
}
