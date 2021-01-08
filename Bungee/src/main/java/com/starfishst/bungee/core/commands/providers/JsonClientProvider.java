package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.providers.type.BungeeExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.io.IOException;
import lombok.NonNull;
import me.googas.messaging.json.client.JsonClient;

public class JsonClientProvider implements BungeeExtraArgumentProvider<JsonClient> {
  @Override
  public @NonNull Class<JsonClient> getClazz() {
    return JsonClient.class;
  }

  @Override
  public @NonNull JsonClient getObject(@NonNull CommandContext context)
      throws ArgumentProviderException {
    try {
      return Guido.getClient().validatedConnection();
    } catch (IOException e) {
      // TODO localize
      throw new ArgumentProviderException("&cThere's no connection with the bot");
    }
  }
}
