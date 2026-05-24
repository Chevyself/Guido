package me.googas.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.starbox.UUIDUtils;

public interface Identifiable {

  default void setRecogString(@NonNull String key, String value) {
    this.setRecog(key, value);
  }

  default void setRecog(@NonNull String key, Object value) {
    if (value == null) {
      this.getRecognition().remove(key);
    } else {
      this.getRecognition().put(key, value);
    }
  }

  /**
   * Check whether this values map equals to another map
   *
   * @param that the map to compare
   * @return true if the maps are the same
   */
  default boolean isSimilar(@NonNull Map<?, ?> that) {
    if (this.getIdentification().isEmpty() || that.isEmpty()) {
      return false;
    } else if (this.getIdentification().size() != that.size()) {
      return false;
    } else {
      for (String key : this.getIdentification().keySet()) {
        if (!that.containsKey(key)
            || that.containsKey(key) && !that.get(key).equals(this.getIdentification().get(key))) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Check whether this values map equals to another map
   *
   * @param that the map to compare
   * @return true if the maps are the same
   */
  default boolean matches(@NonNull Map<?, ?> that) {
    Map<String, Object> recognition = this.getRecognition();
    if (recognition.isEmpty() || that.isEmpty()) return false;
    for (String key : recognition.keySet()) {
      Object object = recognition.get(key);
      if (that.containsKey(key) && (that.get(key) != null && that.get(key).equals(object))
          || (that.get(key) instanceof String
              && object != null
              && ((String) that.get(key)).equalsIgnoreCase(object.toString()))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get an object with the given key aka key in the context
   *
   * @param key the key to getId the value
   * @param typeOfT the type of the value to getId
   * @param <T> the type of the value
   * @return the value if found and the type can cast the value
   */
  default <T> T getId(@NonNull String key, @NonNull Class<T> typeOfT) {
    Map<String, Object> info = this.getIdentification();
    Object t = info.get(key);
    if (t == null) return null;
    if (typeOfT.isAssignableFrom(t.getClass())) return typeOfT.cast(t);
    return null;
  }

  /**
   * @see #getId(String, Class this provides a check to if the object is null
   * @param key the key the getId the value
   * @param def the default value in case the map does not contain it
   * @param typeOfT the type of the value
   * @param <T> the type of the value
   * @return the value if found in the map and if it can be casted else the default value
   */
  @NonNull
  default <T> T getIdOr(@NonNull String key, @NonNull T def, @NonNull Class<T> typeOfT) {
    T t = this.getId(key, typeOfT);
    return t != null ? t : def;
  }

  default long getIdLong(@NonNull String key, long def) {
    return this.getIdOr(key, def, Long.class);
  }

  @NonNull
  default String getIdString(@NonNull String key, @NonNull String def) {
    return this.getIdOr(key, def, String.class);
  }

  /**
   * Get an object with the given key aka key in the context
   *
   * @param key the key to getId the value
   * @param typeOfT the type of the value to getId
   * @param <T> the type of the value
   * @return the value if found and the type can cast the value
   */
  default <T> T getRecog(@NonNull String key, @NonNull Class<T> typeOfT) {
    Map<String, Object> info = this.getRecognition();
    Object t = info.get(key);
    if (t == null) return null;
    if (typeOfT.isAssignableFrom(t.getClass())) return typeOfT.cast(t);
    return null;
  }

  /**
   * @see #getId(String, Class this provides a check to if the object is null
   * @param key the key the getId the value
   * @param def the default value in case the map does not contain it
   * @param typeOfT the type of the value
   * @param <T> the type of the value
   * @return the value if found in the map and if it can be casted else the default value
   */
  @NonNull
  default <T> T getRecogOr(@NonNull String key, @NonNull T def, @NonNull Class<T> typeOfT) {
    T t = this.getRecog(key, typeOfT);
    return t != null ? t : def;
  }

  @NonNull
  default String getRecogString(@NonNull String key, @NonNull String def) {
    return this.getRecogOr(key, def, String.class);
  }

  @NonNull
  default UUID getIdUUID(@NonNull String key, boolean trimmed) {
    String string = this.getIdString(key, "");
    if (string.isEmpty()) throw new IllegalArgumentException(key + " does not contain an uuid");
    if (trimmed) {
      return UUIDUtils.untrim(string);
    } else {
      return UUID.fromString(string);
    }
  }

  @NonNull
  Map<String, Object> getIdentification();

  @NonNull
  default Map<String, Object> getRecognition() {
    return new HashMap<>();
  }
}
