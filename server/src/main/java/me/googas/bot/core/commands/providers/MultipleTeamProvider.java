package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaMultiArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.objects.JoinedStrings;
import lombok.NonNull;
import me.googas.api.matches.team.Team;

public class MultipleTeamProvider implements JdaMultiArgumentProvider<Team> {
  @Override
  public @NonNull Class<Team> getClazz() {
    return Team.class;
  }

  @Override
  public @NonNull Team fromStrings(@NonNull String[] strings, @NonNull CommandContext context)
      throws ArgumentProviderException {
    JoinedStrings joinedStrings = new JoinedStrings(strings);
    String string = joinedStrings.getString();
    return context.get(string, Team.class, context);
  }
}
