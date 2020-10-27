package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** Change the gamemode using t his command */
public class GameModeCommand implements GuidoCommand {

  /** Whether the command is enabled */
  private boolean enabled = false;

  /**
   * Change the gamemode from a player
   *
   * @param sender the sender of the command
   * @param gameMode the gamemode to set the optional or the player to
   * @param optional the optional player to set the gamemode
   * @return the result of the command depending on the execution
   */
  @Command(
      aliases = {"gm", "gamemode"},
      description = "Change your gamemode or someone else",
      permission = "guido.gamemode")
  public Result gamemode(
      Player sender,
      @Required(name = "gamemode", description = "The gamemode to change to") GameMode gameMode,
      @Optional(name = "player", description = "The player to change the gamemode to")
          Player optional) {
    if (optional != null && optional != sender) {
      optional.setGameMode(gameMode);
      return new Result(
          "&a"
              + optional.getName()
              + " &egamemode has been updated to &a"
              + gameMode.toString().toLowerCase());
    } else {
      sender.setGameMode(gameMode);
      return new Result(
          "&eYour gamemode has been updated to &a" + gameMode.toString().toLowerCase());
    }
  }

  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
  }

  @Override
  public @NotNull String getName() {
    return "gamemode";
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
