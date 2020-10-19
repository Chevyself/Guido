package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.guido.api.data.lang.LocaleFile;
import me.googas.commons.maps.Maps;
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
   * @param locale the locale of the sender
   * @param optional the optional player to toggle fly
   * @return the result of the command execution
   */
  @Command(aliases = "fly", description = "Toggle your fly status", permission = "guido.fly")
  public Result fly(
      Player player,
      LocaleFile locale,
      @Optional(name = "player", description = "The player to toggle fly to") Player optional) {
    if (optional != null && optional != player) {
      if (player.hasPermission("guido.fly.else")) {
        if (optional.getAllowFlight()) {
          optional.setAllowFlight(false);
          if (optional.hasPermission("guido.fly")) {
            optional.sendMessage(
                locale.get(
                    "fly.else.disabled",
                    Maps.builder("name", player.getName())
                        .append("display", player.getDisplayName())));
          } else {
            optional.sendMessage(locale.get("fly.disabled"));
          }
          return new Result("&eFly for " + optional.getName() + "  has been disabled");
        } else {
          optional.setAllowFlight(true);
          if (optional.hasPermission("guido.fly")) {
            optional.sendMessage(
                locale.get(
                    "fly.else.enabled",
                    Maps.builder("name", player.getName())
                        .append("display", player.getDisplayName())));
          } else {
            optional.sendMessage(locale.get("fly.enabled"));
          }
          return new Result("&eFly for " + optional.getName() + "  has been enabled");
        }
      } else {
        return new Result(locale.get("fly.else.not-allowed"));
      }
    } else {
      if (player.getAllowFlight()) {
        player.setAllowFlight(false);
        return new Result(locale.get("fly.disabled"));
      } else {
        player.setAllowFlight(true);
        return new Result(locale.get("fly.enabled"));
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
