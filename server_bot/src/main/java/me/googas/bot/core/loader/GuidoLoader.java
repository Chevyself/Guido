package me.googas.bot.core.loader;

import lombok.NonNull;
import me.googas.api.loader.Loader;
import me.googas.bot.core.handlers.GuidoHandler;

public interface GuidoLoader extends Loader, GuidoHandler {
  @NonNull
  GuidoGuildLoader getGuidoGuildLoader();
}
