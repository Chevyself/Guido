package dev.xevy.bukkit;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.context.CommandContext;
import dev.xevy.bukkit.client.BukkitClient;
import dev.xevy.bukkit.lang.BukkitLanguageHandler;
import java.util.Optional;
import lombok.NonNull;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.modules.ModuleRegistry;
import me.googas.starbox.scheduler.Scheduler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public interface GuidoBukkitRuntime extends GuidoClientRuntime {
  @NonNull
  BukkitScheduler getBukkitScheduler();

  @NonNull
  Scheduler getScheduler();

  @NonNull
  CommandManager<CommandContext, BukkitCommand> getCommandManager();

  @NonNull
  BukkitClient getClient();

  @Override
  default @NonNull Optional<JsonClient> getConnection() {
    return this.getClient().getConnection();
  }

  @NonNull
  ModuleRegistry getModuleRegistry();

  @NonNull
  GuidoConfiguration getConfiguration();

  @NonNull
  BukkitLanguageHandler getLanguageHandler();

  @NonNull
  Plugin getPlugin();
}
