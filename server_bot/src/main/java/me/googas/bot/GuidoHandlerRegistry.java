package me.googas.bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.bot.core.GuidoBotRuntime;
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
import me.googas.bot.core.lang.GuidoLanguageHandler;
import me.googas.starbox.logging.LoggerFactory;
import net.dv8tion.jda.api.JDA;

public class GuidoHandlerRegistry {

  @NonNull private static final Logger logger = LoggerFactory.getLogger(GuidoHandlerRegistry.class);

    /** Handlers that must be registered first */
  @NonNull private final Set<GuidoHandler> primaryHandlers;

  @NonNull private final Set<GuidoHandler> defaultHandlers;
  /** The handlers that have been registered */
  @NonNull @Getter private final Set<GuidoHandler> registered = new HashSet<>();

  public GuidoHandlerRegistry(@NonNull GuidoBotRuntime runtime) {
      this.primaryHandlers = Set.of(new GuidoLanguageHandler(runtime));
    this.defaultHandlers =
        Lots.set(
            new DeployHandler(),
            new LinkHandler(runtime),
            new MatchEloCalculator(runtime),
            new MatchMakingChannelsHandler(runtime),
            new MatchMakingHandler(runtime),
            new PGMMatchHandler(),
            new QueueChannelsHandler(runtime),
            new QueueHandler(runtime),
            new RanksHandler(runtime));
  }

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

  public GuidoHandlerRegistry register(@NonNull JDA jda) {
    for (GuidoHandler handler : this.primaryHandlers) {
      this.register(jda, handler);
    }
    for (GuidoHandler handler : this.defaultHandlers) {
      this.register(jda, handler);
    }
    return this;
  }

  public void register(@NonNull JDA jda, @NonNull GuidoHandler handler) {
    handler.register(jda).onEnable();
    this.registered.add(handler);
  }

  public void unregister() {
    for (GuidoHandler handler : this.registered) {
      try {
        handler.onDisable();
      } catch (Throwable e) {
        logger.log(Level.SEVERE, "Failed to disable handler " + handler, e);
      }
      handler.unregister();
    }
    this.registered.clear();
  }

  @NonNull
  public GuidoLanguageHandler getLanguageHandler() {
    return this.getHandler(GuidoLanguageHandler.class);
  }
}
