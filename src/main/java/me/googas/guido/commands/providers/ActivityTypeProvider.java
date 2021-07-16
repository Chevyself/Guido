package me.googas.guido.commands.providers;

import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaArgumentProvider;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Locale;

public class ActivityTypeProvider implements JdaArgumentProvider<Activity.ActivityType> {
  @Override
  public @NonNull Class<Activity.ActivityType> getClazz() {
    return Activity.ActivityType.class;
  }

  @Override
  public @NonNull Activity.ActivityType fromString(
      @NonNull String s, @NonNull CommandContext context) throws ArgumentProviderException {
    try {
      return Activity.ActivityType.valueOf(s.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ArgumentProviderException(s + " is not a valid activity type");
    }
  }
}
