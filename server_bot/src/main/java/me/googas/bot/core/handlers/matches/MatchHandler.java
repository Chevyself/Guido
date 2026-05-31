package me.googas.bot.core.handlers.matches;

import me.googas.bot.core.handlers.GuidoHandler;

/** Handles a match in the given type */
public interface MatchHandler extends GuidoHandler {
  /** Called when a server is ready to host a match */
  void serverReady();
}
