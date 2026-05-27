package com.starfishst.bukkit.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.common.CommandPermission;
import com.github.chevyself.starbox.result.Result;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand implements GuidoCommand {

  private boolean enabled = false;

  @CommandPermission("guido.gamemode")
  @Command(
      aliases = {"gm", "gamemode"},
      description = "Change your gamemode or someone else")
  public Result gamemode(
      Player sender,
      @Required(name = "gamemode", description = "The gamemode to change to") GameMode gameMode,
      @Free(name = "player", description = "The player to change the gamemode to")
          Player optional) {
    if (optional != null && optional != sender) {
      optional.setGameMode(gameMode);
      return Result.of(
          "&a"
              + optional.getName()
              + " &egamemode has been updated to &a"
              + gameMode.toString().toLowerCase());
    } else {
      sender.setGameMode(gameMode);
      return Result.of(
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
