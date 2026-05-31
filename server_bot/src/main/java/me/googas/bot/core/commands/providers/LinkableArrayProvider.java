package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.Linkable;

public class LinkableArrayProvider implements JdaArgumentProvider<Linkable[]> {
  @Override
  public @NonNull Class<Linkable[]> getClazz() {
    return Linkable[].class;
  }

  @Override
  public @NonNull Linkable[] fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    String[] strings = s.split(" ");
    Linkable[] links = new Linkable[strings.length];
    for (int i = 0; i < strings.length; i++) {
      links[i] =
          commandContext
              .getProvidersRegistry()
              .fromString(strings[i], Linkable.class, commandContext);
    }
    return links;
  }
}
