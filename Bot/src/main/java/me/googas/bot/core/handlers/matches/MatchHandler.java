package me.googas.bot.core.handlers.matches;

import me.googas.bot.core.handlers.GuidoEventHandler;

/** Handles a match in the given type */
public interface MatchHandler extends GuidoEventHandler {
  /** Called when a server is ready to host a match */
  void serverReady();
}
