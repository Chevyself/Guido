package com.starfishst.bukkit.handlers;

import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.handlers.placeholders.PlaceholderHandler;
import com.starfishst.bukkit.handlers.scoreboard.ScoreboardHandler;
import com.starfishst.bukkit.handlers.ui.UIHandler;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import org.bukkit.plugin.Plugin;

/** Registers the handlers to use in the plugin */
public class HandlerRegistry {

  /** The plugin to register the handlers on */
  @NonNull private final GuidoPlugin plugin;

  /** The set of default handlers for thes plugin to use */
  @NonNull private final Set<Handler> handlers;
  /** Here's the set containing registered handlers */
  @NonNull private final Set<Handler> registered = new HashSet<>();

  public HandlerRegistry(@NonNull GuidoPlugin plugin) {
    this.plugin = plugin;
    this.handlers = this.getDefaultHandlers(plugin);
  }

  /** Registers all the handlers */
  public void register() {
    this.handlers.addAll(this.getDependencyHandlers());
    for (Handler handler : this.handlers) {
      if (handler.isEnabled()) {
        handler.register(this.plugin).onEnable();
        this.registered.add(handler);
        if (handler.hasReceptors()) {
          this.plugin.getClient().addReceptors(handler);
        }
        this.plugin.getLogger().info(handler.getName() + " has been registered");
      }
    }
  }

  public void unregister() {
    for (Handler handler : this.registered) {
      handler.onDisable();
      handler.unregister();
    }
    this.registered.clear();
  }

  public void unregister(@NonNull Handler handler) {
    if (this.registered.remove(handler)) {
      handler.onDisable();
      handler.unregister();
    }
  }

  @Nullable
  public <T extends Handler> T getHandler(@NonNull Class<T> clazz) {
    for (Handler handler : this.registered) {
      if (clazz.isAssignableFrom(handler.getClass())) {
        return clazz.cast(handler);
      }
    }
    return null;
  }

  @NonNull
  public <T extends Handler> T requireHandler(@NonNull Class<T> clazz) {
    return Validate.notNull(this.getHandler(clazz), clazz + " handler was not registered");
  }

  @NonNull
  private Set<Handler> getDefaultHandlers(@NonNull GuidoPlugin plugin) {
    return Lots.set(
        new PlaceholderHandler(),
        new ScoreboardHandler(),
        new UIHandler(),
        new CommandExecutionHandler(),
        new DecorationsHandler(),
        new GroupsHandler(),
        new PermissionHandler(plugin),
        new SpawnHandler(),
        new TestHandler());
  }

  /** This method will get the {@link Dependency#getHandlers(Plugin)} and add them to a new set */
  public Set<Handler> getDependencyHandlers() {
    Set<Handler> handlers = new HashSet<>();
    for (Dependency dependency : this.plugin.getDependencies().getDependencies()) {
      if (dependency.isEnabled()) {
        handlers.addAll(dependency.getHandlers(this.plugin));
      }
    }
    return handlers;
  }
}
