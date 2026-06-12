package com.starfishst.bukkit.commands.providers;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import lombok.NonNull;
import me.googas.net.sockets.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;

public class JsonClientProvider implements BukkitExtraArgumentProvider<JsonClient> {
  @NonNull private final GuidoBukkitRuntime runtime;

  public JsonClientProvider(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

  @NotNull
  @Override
  public JsonClient getObject(@NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    return runtime.getConnection().orElseThrow();
  }

  @Override
  public @NonNull Class<JsonClient> getClazz() {
    return JsonClient.class;
  }
}
