package me.googas.api;

import lombok.NonNull;
import me.googas.api.loader.Loader;
import me.googas.net.cache.MemoryCache;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.scheduler.Scheduler;

public interface GuidoInstance {

  @NonNull
  MemoryCache getCache();

  @NonNull
  ListenerManager getListenerManager();

  @NonNull
  Scheduler getScheduler();

  @NonNull
  Loader getLoader();

  @NonNull
  API.Messenger getMessenger();
}
