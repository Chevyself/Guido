package me.googas.api.lang;

import java.util.Map;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;

/** An entity which can be sent localized messages */
public interface Localized {

  /**
   * Send a message to this linked data
   *
   * @param message the message to send
   */
  void sendMessage(@NotNull String message);

  /**
   * Send a localized message using a given key
   *
   * @param key the key of the localized message
   */
  void sendLocalized(@NotNull String key);

  /**
   * Send a localized message using the given key and placeholders
   *
   * @param key the key of the localized message
   * @param placeholders the placeholders of the message
   */
  void sendLocalized(@NotNull String key, @NotNull Map<String, String> placeholders);

  /**
   * Send a localized message using the given key and placeholders
   *
   * @param key the key of the localized message
   * @param placeholders the placeholders of the message as a map builder
   */
  default void sendLocalized(
      @NotNull String key, @NotNull MapBuilder<String, String> placeholders) {
    this.sendLocalized(key, placeholders.build());
  }
}
