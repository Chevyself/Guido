package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.TeamData;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.core.Guido;

public class TeamDataProvider implements JdaArgumentProvider<TeamData> {
  @Override
  public @NonNull Class<TeamData> getClazz() {
    return TeamData.class;
  }

  @Override
  public @NonNull TeamData fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    BotDataLoader loader = Guido.getDataLoader();
    TeamData team = loader.getTeam(s);
    if (team != null) return team;
    team = loader.getTeamByName(s.replace("_", " "));
    if (team != null) return team;
    // TODO localize
    throw new ArgumentProviderException(s + " is not a valid team");
  }
}
