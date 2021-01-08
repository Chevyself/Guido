package me.googas.bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.bot.api.DiscordLoader;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.lang.GuidoLanguageHandler;
import me.googas.bot.core.loader.GuidoFallbackLoader;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.loader.jsongo.JsongoLoader;
import me.googas.commons.ProgramArguments;
import net.dv8tion.jda.api.JDA;

public class GuidoHandlerRegistry {

  /** Handlers that must be registered first */
  @NonNull private final Set<GuidoHandler> primaryHandlers = new HashSet<>();

  @NonNull private final Set<GuidoHandler> defaultHandlers = new HashSet<>();
  /** The handlers that have been registered */
  @NonNull private final Set<GuidoHandler> registered = new HashSet<>();

  /**
   * Get a handler using its class. This will loop and check if the class of the handler matches the
   * one queried
   *
   * @param clazz the class to match
   * @param <T> the type for guido handlers
   * @return the guido handler
   * @throws IllegalStateException if the handler was not found
   */
  public <T extends GuidoHandler> T getHandler(@NonNull Class<T> clazz) {
    for (GuidoHandler handler : this.registered) {
      if (clazz.isAssignableFrom(handler.getClass())) {
        return clazz.cast(handler);
      }
    }
    throw new IllegalStateException("The handler with class " + clazz + " has not been added yet");
  }

  /**
   * Get the list of handlers that implement a class of guido handler
   *
   * @param tClass the class of guido handler that they must implement
   * @param <T> the type of the guido handler
   * @return the list of handlers
   */
  @NonNull
  public <T extends GuidoHandler> List<T> getHandlers(@NonNull Class<T> tClass) {
    List<T> handlers = new ArrayList<>();
    for (GuidoHandler handler : this.registered) {
      if (tClass.isAssignableFrom(handler.getClass())) {
        handlers.add(tClass.cast(handler));
      }
    }
    return handlers;
  }

  public void register(@NonNull JDA jda) {
    for (GuidoHandler handler : this.primaryHandlers) {
      handler.register(jda).onEnable();
      this.registered.add(handler);
    }
    for (GuidoHandler handler : this.defaultHandlers) {
      handler.register(jda).onEnable();
      this.registered.add(handler);
    }
  }

  @NonNull
  public GuidoHandlerRegistry setupLoader(@NonNull ProgramArguments arguments) {
    // TODO check if a loader has already been registered and unregister it
    GuidoLoader loader = new GuidoFallbackLoader();
    if (arguments.containsKey("loader")) {
      String loaderName = arguments.getProperty("loader");
      if (loaderName.equalsIgnoreCase("jsongo")) {
        try {
          loader =
              new JsongoLoader(
                  arguments.getProperty("uri", "none"),
                  arguments.getProperty("database", "testing-database"));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    this.primaryHandlers.add(new GuidoLanguageHandler(loader).load("en"));
    this.primaryHandlers.add(loader);
    return this;
  }

  public void unregister() {
    for (GuidoHandler handler : this.registered) {
      try {
        handler.onDisable();
      } catch (Throwable e) {
        e.printStackTrace();
      }
      handler.unregister();
    }
    this.registered.clear();
  }

  @NonNull
  public GuidoHandlerRegistry setupDiscordLoader() {
    this.primaryHandlers.add(new GuidoDiscordFileLoader());
    return this;
  }

  @NonNull
  public GuidoLanguageHandler getLanguageHandler() {
    return this.getHandler(GuidoLanguageHandler.class);
  }

  @NonNull
  public DiscordLoader getDiscordLoader() {
    return this.getHandler(DiscordLoader.class);
  }

  @NonNull
  public GuidoLoader getLoader() {
    return this.getHandler(GuidoLoader.class);
  }
}
