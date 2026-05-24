package me.googas.api.lang;

import java.util.Map;
import lombok.NonNull;
import me.googas.api.utility.MapBuilder;
import me.googas.starbox.Strings;

/** The file of localized messages */
public interface LocaleFile {

  /** Saves the locale file */
  void save();

  /**
   * Get the raw string from the file
   *
   * @param path the path to the string
   * @return the string if the path leads to one else null
   */
  String getRaw(@NonNull String path);

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @NonNull
  default String get(@NonNull String path) {
    String raw = this.getRaw(path);
    return raw == null ? path : raw;
  }

  /**
   * Get the string and build it with placeholders. It will replace the placeholders that are inside
   * a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @NonNull
  default String get(@NonNull String path, @NonNull Map<String, String> placeholders) {
    return Strings.format(this.get(path), placeholders);
  }

  /**
   * Get the string and build it with placeholders using a builder. It will replace the placeholders
   * that are inside a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @NonNull
  default String get(@NonNull String path, @NonNull MapBuilder<String, String> placeholders) {
    return Strings.format(this.get(path), placeholders);
  }

  /**
   * Get the language that this file provides
   *
   * @return the language that this entity is provides
   */
  @NonNull
  String getLang();
}
