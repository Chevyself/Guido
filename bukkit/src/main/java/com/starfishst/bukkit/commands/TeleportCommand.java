package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class TeleportCommand implements GuidoCommand {

  private boolean enabled = false;

  @Command(
      aliases = {"teleport", "tp"},
      description = "Teleport to a player",
      permission = "guido.teleport")
  public void teleport(
      Player sender,
      @Required(name = "player", description = "The player to teleport to") Player player,
      @Optional(name = "destination", description = "The player to teleport the first player to")
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
