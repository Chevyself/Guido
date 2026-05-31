package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.team.Team;

public class MultipleTeamProvider implements JdaArgumentProvider<Team> {
  @Override
  public @NonNull Class<Team> getClazz() {
    return Team.class;
  }

  @Override
  public @NonNull Team fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    return commandContext.getProvidersRegistry().fromString(s, Team.class, commandContext);
  }
}
