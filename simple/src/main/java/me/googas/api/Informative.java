package me.googas.api;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

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
  default <T> T get(String context, @NonNull String key, @NonNull Class<T> typeOfT) {
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
      String context, @NonNull String key, @NonNull T def, @NonNull Class<T> typeOfT) {
    T t = this.get(context, key, typeOfT);
    return t != null ? t : def;
  }

  /**
   * Set a value in the map of the context
   *
   * @param context the context to getId the map
   * @param key the key to set the value
   * @param value the value that will be assigned to the key
   */
  default void set(String context, @NonNull String key, Object value) {
    if (context == null) {
      context = "global";
    }
    if (value == null) {
      this.getInformation(context).remove(key);
    } else {
      this.getInformation(context).put(key, value);
    }
  }

  default String getString(String context, @NonNull String key, @NonNull String def) {
    return this.getOr(context, key, def, String.class);
  }

  /**
   * Get the map of information in a given context
   *
   * @param context the context to getId the map of
   * @return the map of the given context
   */
  @NonNull
  default Map<String, Object> getInformation(String context) {
    if (context == null) {
      context = "global";
    }
    Map<String, Object> map = this.getInformation().get(context);
    if (map == null) return new HashMap<>();
    return map;
  }

  default void setString(String context, @NonNull String key, String value) {
    this.set(context, key, value);
  }

  default boolean getBoolean(String context, @NonNull String key, boolean def) {
    return this.getOr(context, key, def, Boolean.class);
  }

  default long getLong(String context, @NonNull String key, long def) {
    return this.getNumber(context, key, def).longValue();
  }

  default Number getNumber(String context, @NonNull String key, @NonNull Number def) {
    return this.getOr(context, key, def, Number.class);
  }

  default int getInt(String context, @NonNull String key, int def) {
    return this.getNumber(context, key, def).intValue();
  }

  default float getFloat(String context, @NonNull String key, float def) {
    return this.getNumber(context, key, def).floatValue();
  }

  default void setBoolean(String context, @NonNull String key, boolean value) {
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
