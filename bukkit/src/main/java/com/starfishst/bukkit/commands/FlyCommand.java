package com.starfishst.bukkit.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.common.CommandPermission;
import com.github.chevyself.starbox.result.Result;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.utility.Maps;
import org.bukkit.entity.Player;

/** Commands used for flight */
public class FlyCommand implements GuidoCommand {

  private boolean enabled = false;

  @CommandPermission("guido.fly")
  @Command(aliases = "fly", description = "Toggle your fly status")
  public Result fly(
      Player player,
      LocaleFile locale,
      @Free(name = "player", description = "The player to toggle fly to") Player optional) {
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
          return Result.of("&eFly for " + optional.getName() + "  has been disabled");
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
          return Result.of("&eFly for " + optional.getName() + "  has been enabled");
        }
      } else {
        return Result.of(locale.get("fly.else.not-allowed"));
      }
    } else {
      if (player.getAllowFlight()) {
        player.setAllowFlight(false);
        return Result.of(locale.get("fly.disabled"));
      } else {
        player.setAllowFlight(true);
        return Result.of(locale.get("fly.enabled"));
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
