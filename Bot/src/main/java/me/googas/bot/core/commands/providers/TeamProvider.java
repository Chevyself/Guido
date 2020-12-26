package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.team.Team;
import me.googas.bot.Guido;
import me.googas.bot.api.types.loader.BotDataLoader;
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
    BotDataLoader loader = Guido.getDataLoader();
    Team team = loader.getTeam(s);
    if (team != null) return team;
    team = loader.getTeamByName(s.replace("_", " "));
    if (team != null) return team;
    throw Lang.getException("invalid.team", Maps.singleton("string", s), context);
  }
}
