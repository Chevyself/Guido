package me.googas.api;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Validate;

/**
 * When objects are able to contain information this should be implemented by it as it provides many
 * methods to query such information
 */
public interface Informative {

  /**
   * Get an object with the given key aka key in the context
   *
   * @param context the context to getId the map
   * @param key the key to getId the value
   * @param typeOfT the type of the value to getId
   * @param <T> the type of the value
   * @return the value if found and the type can cast the value
   */
  @Nullable
  default <T> T get(@Nullable String context, @NonNull String key, @NonNull Class<T> typeOfT) {
    Map<String, Object> info = this.getInformation(context);
    Object t = info.get(key);
    if (t == null) return null;
    if (typeOfT.isAssignableFrom(t.getClass())) return typeOfT.cast(t);
    return null;
  }

  /**
   * @see #get(String, String, Class) this provides a check to if the object is null
   * @param context the context to getId the map
   * @param key the key the getId the value
   * @param def the default value in case the map does not contain it
   * @param typeOfT the type of the value
   * @param <T> the type of the value
   * @return the value if found in the map and if it can be casted else the default value
   */
  @NonNull
  default <T> T getOr(
      @Nullable String context, @NonNull String key, @NonNull T def, @NonNull Class<T> typeOfT) {
    return Validate.notNullOr(this.get(context, key, typeOfT), def);
  }

  /**
   * Set a value in the map of the context
   *
   * @param context the context to getId the map
   * @param key the key to set the value
   * @param value the value that will be assigned to the key
   */
  default void set(@Nullable String context, @NonNull String key, @Nullable Object value) {
    if (context == null) {
      context = "global";
    }
    if (value == null) {
      this.getInformation(context).remove(key);
    } else {
      this.getInformation(context).put(key, value);
    }
  }

  default String getString(@Nullable String context, @NonNull String key, @NonNull String def) {
    return this.getOr(context, key, def, String.class);
  }

  /**
   * Get the map of information in a given context
   *
   * @param context the context to getId the map of
   * @return the map of the given context
   */
  @NonNull
  default Map<String, Object> getInformation(@Nullable String context) {
    if (context == null) {
      context = "global";
    }
    Map<String, Object> map = this.getInformation().get(context);
    if (map == null) return new HashMap<>();
    return map;
  }

  default void setString(@Nullable String context, @NonNull String key, @Nullable String value) {
    this.set(context, key, value);
  }

  default boolean getBoolean(@Nullable String context, @NonNull String key, boolean def) {
    return this.getOr(context, key, def, Boolean.class);
  }

  default long getLong(@Nullable String context, @NonNull String key, long def) {
    return this.getNumber(context, key, def).longValue();
  }

  default Number getNumber(@Nullable String context, @NonNull String key, @NonNull Number def) {
    return this.getOr(context, key, def, Number.class);
  }

  default int getInt(@Nullable String context, @NonNull String key, int def) {
    return this.getNumber(context, key, def).intValue();
  }

  default float getFloat(@Nullable String context, @NonNull String key, float def) {
    return this.getNumber(context, key, def).floatValue();
  }

  default void setBoolean(@Nullable String context, @NonNull String key, boolean value) {
    this.set(context, key, value);
  }

  default void remove(@NonNull String context, @NonNull String key) {
    this.getInformation(context).remove(key);
  }

  default double getDouble(@NonNull String context, @NonNull String key, double def) {
    return this.getNumber(context, key, def).doubleValue();
  }

  /**
   * Get the information map which has the context and its map
   *
   * @return the information map
   */
  @NonNull
  Map<String, Map<String, Object>> getInformation();
}
