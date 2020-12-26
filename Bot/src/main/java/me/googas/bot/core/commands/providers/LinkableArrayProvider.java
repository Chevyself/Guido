package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaMultiArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.Linkable;

public class LinkableArrayProvider implements JdaMultiArgumentProvider<Linkable[]> {
  @Override
  public @NonNull Class<Linkable[]> getClazz() {
    return Linkable[].class;
  }

  @Override
  public Linkable @NonNull [] fromStrings(
      @NonNull String[] strings, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    Linkable[] links = new Linkable[strings.length];
    for (int i = 0; i < strings.length; i++) {
      links[i] = commandContext.get(strings[i], Linkable.class, commandContext);
    }
    return links;
  }
}
