package com.starfishst.bukkit.commands.providers;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.starfishst.bukkit.BukkitMinecraftPlayer;
import dev.xevy.guido.mc.MinecraftPlayer;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MinecraftPlayerProvider implements BukkitExtraArgumentProvider<MinecraftPlayer> {
  @NotNull
  @Override
  public MinecraftPlayer getObject(@NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    Player player = commandContext.getObject(Player.class, commandContext);
    return new BukkitMinecraftPlayer(player);
  }

  @Override
  public @NonNull Class<MinecraftPlayer> getClazz() {
    return MinecraftPlayer.class;
  }
}
