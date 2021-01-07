package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.team.Team;
import me.googas.bot.api.Guido;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

public class TeamProvider implements JdaArgumentProvider<Team> {
  @Override
  public @NonNull Class<Team> getClazz() {
    return Team.class;
  }

  @Override
  public @NonNull Team fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    Team team = loader.getTeams().getTeam(s);
    if (team != null) return team;
    team = loader.getTeams().getTeamByName(s.replace("_", " "));
    if (team != null) return team;
    throw Lang.getException("invalid.team", Maps.singleton("string", s), context);
  }
}
