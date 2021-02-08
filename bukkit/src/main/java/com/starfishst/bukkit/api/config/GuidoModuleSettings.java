package com.starfishst.bukkit.api.config;

import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Validate;
import org.bukkit.configuration.ConfigurationSection;

/** The settings for a listener */
public interface GuidoModuleSettings {

  default double getDouble(@NonNull String key, double def) {
    return this.getNumber(key, def).doubleValue();
  }

  @NonNull
  default Number getNumber(@NonNull String key, @NonNull Number def) {
    return this.getOr(key, def, Number.class);
  }

  default int getInt(@NonNull String key, int def) {
    return this.getNumber(key, def).intValue();
  }

  /**
   * Get the name of the listener to which this are settings
   *
   * @return the name of the listener
   */
  @NonNull
  String getName();

  @Nullable
  default <T> T get(@NonNull String key, @NonNull Class<T> clazz) {
    Object obj = this.getMap().get(key);
    if (obj != null) {
      if (clazz.isAssignableFrom(obj.getClass())) return clazz.cast(obj);
    }
    return null;
  }

  @NonNull
  default <T> T getOr(@NonNull String key, @NonNull T def, @NonNull Class<T> clazz) {
    return Validate.notNullOr(this.get(key, clazz), def);
  }

  default boolean getBoolean(@NonNull String key, boolean def) {
    return this.getOr(key, def, Boolean.class);
  }

  @NonNull
  Map<String, Object> getMap();

  @Nullable
  ConfigurationSection getSection();
}
