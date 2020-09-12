package com.starfishst.guido.pgm.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.guido.pgm.api.commands.GuidoCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** The command to get the ping of a player */
public class PingCommand implements GuidoCommand {

  /** Whether the command is enabled */
  private boolean enabled = false;

  /**
   * Get the ping of a player
   *
   * @param player the player executor of the command
   * @param optional the optional player to get the ping from
   * @return the result of the command execution
   */
  @Command(aliases = "ping", description = "Get the ping of a player", permission = "guido.ping")
  public Result ping(
      Player player,
      @Optional(name = "player", description = "The player to get the ping from") Player optional) {
    if (optional != null && optional != player) {
      return new Result(
          "&a" + optional.getName() + " &eping is &a" + optional.spigot().getPing() + "ms");
    } else {
      return new Result("&eYour ping is &a" + player.spigot().getPing() + "ms");
    }
  }

  @Override
  public @NotNull String getName() {
    return "ping";
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
