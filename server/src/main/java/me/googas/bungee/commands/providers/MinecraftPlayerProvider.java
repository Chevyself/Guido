package me.googas.bungee.commands.providers;

import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.providers.type.BungeeExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.guido.mc.MinecraftPlayer;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jspecify.annotations.NonNull;

public class MinecraftPlayerProvider implements BungeeExtraArgumentProvider<MinecraftPlayer> {

  @Override
  public @NonNull MinecraftPlayer getObject(@NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    ProxiedPlayer proxied = commandContext.getObject(ProxiedPlayer.class, commandContext);
    return new MinecraftPlayer() {
      @Override
      public @NonNull UUID getUniqueId() {
        return proxied.getUniqueId();
      }

      @Override
      public @NonNull String getLocale() {
        return proxied.getLocale().toString().split("_")[0];
      }
    };
  }

  @Override
  public @NonNull Class<MinecraftPlayer> getClazz() {
    return MinecraftPlayer.class;
  }
}
