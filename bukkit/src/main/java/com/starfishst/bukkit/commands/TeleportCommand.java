package com.starfishst.bukkit.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.common.CommandPermission;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class TeleportCommand implements GuidoCommand {

  private boolean enabled = false;

  @CommandPermission("guido.teleport")
  @Command(
      aliases = {"teleport", "tp"},
      description = "Teleport to a player")
  public void teleport(
      Player sender,
      @Required(name = "player", description = "The player to teleport to") Player player,
      @Free(name = "destination", description = "The player to teleport the first player to")
          Player to) {
    if (to != null) {
      if (sender.hasPermission("guido.teleport.else")) {
        player.teleport(to);
      }
    } else {
      sender.teleport(player);
    }
  }

  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
  }

  @Override
  public @NonNull String getName() {
    return "teleport";
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
