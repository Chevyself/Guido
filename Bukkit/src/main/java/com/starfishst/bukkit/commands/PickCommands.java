package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.pgm.matches.HostedMatch;
import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import com.starfishst.bukkit.listeners.pgm.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.listeners.pgm.matches.creation.TeamCreation;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Required;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.TeamMember;
import me.googas.commons.maps.Maps;

public class PickCommands implements GuidoCommand {

  @Command(aliases = "pick", description = "pick.desc")
  public Result pick(
      HostedMatch match,
      BukkitLocaleFile locale,
      TeamMember captain,
      @Required(name = "pick.player", description = "pick.player.desc") LinkableInfo info) {
    PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
    if (listener != null) {
      TeamCreation creation = listener.getCreation("pick");
      if (creation instanceof PickTeamSelection) {
        if (((PickTeamSelection) creation).isPicking(match.getId(), captain)) {
          ((PickTeamSelection) creation).pick(match.getId(), captain, info);
          return new Result(
              locale.get(
                  "pick.success",
                  Maps.singleton(
                      "name", info.getIdentification().getOr("nickname", String.class, ""))));
        } else {
          return new Result(locale.get("pick.not-picking"));
        }
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
