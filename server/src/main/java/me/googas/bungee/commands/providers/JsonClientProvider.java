package me.googas.bungee.commands.providers;

import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.providers.type.BungeeExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import me.googas.bungee.GuidoBungee;
import me.googas.net.sockets.json.client.JsonClient;
import org.jspecify.annotations.NonNull;

public class JsonClientProvider implements BungeeExtraArgumentProvider<JsonClient> {
  @Override
  public @NonNull JsonClient getObject(@NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    return GuidoBungee.getClient();
  }

  @Override
  public @NonNull Class<JsonClient> getClazz() {
    return JsonClient.class;
  }
}
