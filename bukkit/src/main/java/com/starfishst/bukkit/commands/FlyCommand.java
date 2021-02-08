package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.commons.maps.Maps;
import org.bukkit.entity.Player;

/** Commands used for flight */
public class FlyCommand implements GuidoCommand {

  private boolean enabled = false;

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
  public @NonNull String getName() {
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
