package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand implements GuidoCommand {

  private boolean enabled = false;

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
  public @NonNull String getName() {
    return "gamemode";
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
