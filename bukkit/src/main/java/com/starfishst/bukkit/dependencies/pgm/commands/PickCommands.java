package com.starfishst.bukkit.dependencies.pgm.commands;

import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.commands.GuidoCommand;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedPlayer;
import com.starfishst.bukkit.dependencies.pgm.PGMLeader;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.TeamCreation;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import com.starfishst.core.annotations.Required;
import lombok.NonNull;
import me.googas.commons.maps.Maps;

public class PickCommands implements GuidoCommand {

  @Command(aliases = "pick", description = "pick.desc")
  public Result pick(
      PGMHostedMatch match,
      BukkitLocaleFile locale,
      PGMLeader leader,
      @Required(name = "pick.player", description = "pick.player.desc") PGMHostedPlayer player) {
    PGMMatchMakingHandler listener = Guido.getModuleRegistry().require(PGMMatchMakingHandler.class);
    TeamCreation creation = listener.getCreation("pick");
    if (creation instanceof PickTeamSelection) {
      if (((PickTeamSelection) creation).isPicking(match.getId(), leader.validated())) {
        ((PickTeamSelection) creation).pick(match.getId(), leader.validated(), player.validated());
        return new Result(
            locale.get(
                "pick.success", Maps.singleton("name", player.getRecogString("nickname", ""))));
      } else {
        return new Result(locale.get("pick.not-picking"));
      }
    }
    return new Result(locale.get("pick.not-match"));
  }

  /**
   * Set whether the command is enabled
   *
   * @param bol the new value
   */
  @Override
  public void setEnabled(boolean bol) {}

  /**
   * Get the name of the command. This is used to enable it or not from the configuration
   *
   * @return the name of the command
   */
  @Override
  public @NonNull String getName() {
    return "pick";
  }

  /**
   * Get whether the command is enabled
   *
   * @return true if the command is enabled
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
