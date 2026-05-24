package me.googas.api.lang;

import java.util.Map;
import lombok.NonNull;

/** An entity which can be sent localized messages */
public interface Localized {

  /**
   * Send a message to this linked data
   *
   * @param message the message to send
   */
  void sendMessage(@NonNull String message);

  /**
   * Send a localized message using a given key
   *
   * @param key the key of the localized message
   */
  void sendLocalized(@NonNull String key);

  /**
   * Send a localized message using the given key and placeholders
   *
   * @param key the key of the localized message
   * @param placeholders the placeholders of the message
   */
  void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders);

  /**
   * Set the lang that this localized prefers
   *
   * @param lang the language
   */
  void setLang(@NonNull String lang);

  /**
   * Get the language of this localized entity
   *
   * @return the language as a string
   */
  @NonNull
  String getLang();
}
