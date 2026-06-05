package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import org.jetbrains.annotations.NotNull;

public class MinecraftTeamSelectionTypeProvider
    implements JdaArgumentProvider<MinecraftTeamSelectionType> {
  @NotNull
  @Override
  public MinecraftTeamSelectionType fromString(
      @NonNull String string, @NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    try {
      return MinecraftTeamSelectionType.valueOf(string.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ArgumentProviderException(
          String.format("%s did not match any selection types", string));
    }
  }

  @Override
  public @NonNull Class<MinecraftTeamSelectionType> getClazz() {
    return MinecraftTeamSelectionType.class;
  }
}
