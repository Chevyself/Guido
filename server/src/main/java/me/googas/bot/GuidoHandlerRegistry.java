package me.googas.bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.bot.api.DiscordLoader;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.deploy.DeployHandler;
import me.googas.bot.core.handlers.link.LinkHandler;
import me.googas.bot.core.handlers.matches.MatchEloCalculator;
import me.googas.bot.core.handlers.matches.MatchMakingChannelsHandler;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.matches.PGMMatchHandler;
import me.googas.bot.core.handlers.queue.QueueChannelsHandler;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.bot.core.handlers.ranks.RanksHandler;
import me.googas.bot.core.handlers.responsive.GuidoMessagesController;
import me.googas.bot.core.handlers.test.TestHandler;
import me.googas.bot.core.lang.GuidoLanguageHandler;
import me.googas.bot.core.loader.GuidoFallbackLoader;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.loader.jsongo.JsongoLoader;
import me.googas.starbox.ProgramArguments;
import net.dv8tion.jda.api.JDA;

public class GuidoHandlerRegistry {

  /** Handlers that must be registered first */
  @NonNull private final Set<GuidoHandler> primaryHandlers = new HashSet<>();

  @NonNull
  private final Set<GuidoHandler> defaultHandlers =
      Lots.set(
          new DeployHandler(),
          new LinkHandler(),
          new MatchEloCalculator(),
          new MatchMakingChannelsHandler(),
          new MatchMakingHandler(),
          new PGMMatchHandler(),
          new QueueChannelsHandler(),
          new QueueHandler(),
          new RanksHandler(),
          new GuidoMessagesController(),
          new TestHandler());
  /** The handlers that have been registered */
  @NonNull @Getter private final Set<GuidoHandler> registered = new HashSet<>();

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
      this.register(jda, handler);
    }
    for (GuidoHandler handler : this.defaultHandlers) {
      this.register(jda, handler);
    }
  }

  public void register(@NonNull JDA jda, @NonNull GuidoHandler handler) {
    handler.register(jda).onEnable();
    this.registered.add(handler);
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
