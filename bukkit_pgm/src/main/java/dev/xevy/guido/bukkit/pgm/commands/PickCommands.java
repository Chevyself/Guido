package dev.xevy.guido.bukkit.pgm.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import dev.xevy.bukkit.BukkitLocaleFile;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.bukkit.GuidoCommand;
import dev.xevy.guido.bukkit.pgm.PGMHostedMatch;
import dev.xevy.guido.bukkit.pgm.PGMHostedPlayer;
import dev.xevy.guido.bukkit.pgm.PGMLeader;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import dev.xevy.guido.bukkit.pgm.listeners.matches.creation.PickTeamSelection;
import dev.xevy.guido.bukkit.pgm.listeners.matches.creation.TeamCreation;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.utility.Maps;

public class PickCommands implements GuidoCommand {

  @Command(aliases = "pick", description = "pick.desc")
  public Result pick(
      PGMHostedMatch match,
      BukkitLocaleFile locale,
      PGMLeader leader,
      GuidoBukkitRuntime runtime,
      @Required(name = "pick.player", description = "pick.player.desc") PGMHostedPlayer player) {
    PGMMatchMakingHandler listener =
        runtime.getModuleRegistry().require(PGMMatchMakingHandler.class);
    TeamCreation creation = listener.getCreation(MinecraftTeamSelectionType.PICK).orElse(null);
    if (creation instanceof PickTeamSelection) {
      if (((PickTeamSelection) creation).isPicking(match.getId(), leader.validated())) {
        ((PickTeamSelection) creation).pick(match.getId(), leader.validated(), player.validated());
        return Result.of(locale.get("pick.success", Maps.singleton("name", player.getNickname())));
      } else {
        return Result.of(locale.get("pick.not-picking"));
      }
    }
    return Result.of(locale.get("pick.not-match"));
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
