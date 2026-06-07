package me.googas.bot.core.loader;

import lombok.NonNull;
import me.googas.api.loader.Loader;

public interface GuidoLoader extends Loader {
  @NonNull
  GuidoGuildLoader getGuidoGuildLoader();
}
