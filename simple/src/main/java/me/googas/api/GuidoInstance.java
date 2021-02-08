package me.googas.api;

import lombok.NonNull;
import me.googas.api.loader.Loader;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.scheduler.Scheduler;

public interface GuidoInstance {

  @NonNull
  MemoryCache getCache();

  @NonNull
  Fallback getFallback();

  @NonNull
  ListenerManager getListenerManager();

  @NonNull
  Scheduler getScheduler();

  @NonNull
  Loader getLoader();

  @NonNull
  API.Messenger getMessenger();
}
