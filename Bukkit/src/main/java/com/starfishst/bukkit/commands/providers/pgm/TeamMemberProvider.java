package com.starfishst.bukkit.commands.providers.pgm;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.pgm.matches.HostedMatch;
import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import com.starfishst.bukkit.listeners.pgm.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.listeners.pgm.matches.creation.TeamCreation;
import com.starfishst.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import me.googas.api.matches.TeamMember;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated This provider is terminar and might be deleted in the future it only should be used
 *     in {@link com.starfishst.bukkit.commands.PickCommands}
 */
public class TeamMemberProvider implements BukkitExtraArgumentProvider<TeamMember> {

  @Override
  public @NotNull Class<TeamMember> getClazz() {
    return TeamMember.class;
  }

  @NotNull
  @Override
  public TeamMember getObject(@NotNull CommandContext context) throws ArgumentProviderException {
    PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
    BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(context);
    if (context.getSender() instanceof Player) {
      if (listener != null) {
        TeamCreation pick = listener.getCreation("pick");
        HostedMatch match = listener.getMatch(context.getSender());
        if (match == null) throw new IllegalArgumentException(locale.get("participant-only"));
        if (pick instanceof PickTeamSelection) {
          for (PickTeamSelection.SelectingTeam team :
              ((PickTeamSelection) pick).getTeams(match.getId())) {
            if (team.getLeaderUniqueId().equals(((Player) context.getSender()).getUniqueId())) {
              return team.getLeader();
            }
          }
        }
      }
    }
    throw new ArgumentProviderException(locale.get("captain-only"));
  }
}
