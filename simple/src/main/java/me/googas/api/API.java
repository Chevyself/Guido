package me.googas.api;

import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.links.Linkable;
import me.googas.api.loader.Loader;
import me.googas.commons.Validate;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.scheduler.Scheduler;

public class API {

  @Nullable private static GuidoInstance instance;

  public static void setInstance(@Nullable GuidoInstance instance) {
    API.instance = instance;
  }

  @NonNull
  private static GuidoInstance getInstance() {
    return Validate.notNull(API.instance, "API instance may have not been initialized");
  }

  public static @NonNull MemoryCache getCache() {
    return API.getInstance().getCache();
  }

  public static @NonNull Fallback getFallback() {
    return API.getInstance().getFallback();
  }

  public static @NonNull ListenerManager getListenerManager() {
    return API.getInstance().getListenerManager();
  }

  public static @NonNull Scheduler getScheduler() {
    return API.getInstance().getScheduler();
  }

  public static @NonNull Loader getLoader() {
    return API.getInstance().getLoader();
  }

  @NonNull
  public static API.Messenger getMessenger() {
    return API.getInstance().getMessenger();
  }

  public interface Messenger {

    @NonNull
    String getSingle(@NonNull Linkable linkable);

    void sendMessage(@NonNull Linkable linkable, @NonNull String message);

    void sendLocalized(@NonNull Linkable linkable, @NonNull String key);

    void sendLocalized(
        @NonNull Linkable linkable, @NonNull String key, @NonNull Map<String, String> placeholders);
  }
}
