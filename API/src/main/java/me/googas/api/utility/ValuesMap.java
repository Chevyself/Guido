package me.googas.api.utility;

import java.util.Map;
import me.googas.commons.Atomic;
import me.googas.commons.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents certain changes and configurations for an object */
public interface ValuesMap {

  /**
   * Get the value of the given key
   *
   * @param key the key to get the value from
   * @return the value or null if the map does not contain it
   */
  @Nullable
  default Object getValue(@NotNull String key) {
    return this.getMap().get(key);
  }

  /**
   * Get the preference casted to certain class or null if the preference is not inside the map
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  @Nullable
  default <T> T getValue(@NotNull String name, @NotNull Class<T> clazz) {
    Object obj = this.getMap().get(name);
    if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
      return clazz.cast(obj);
    } else {
      return null;
    }
  }

  /**
   * Get the preference casted to certain class or null if the preference is not inside the map
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  @NotNull
  default <T> T getValidatedValue(@NotNull String name, @NotNull Class<T> clazz) {
    Object obj = this.getMap().get(name);
    if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
      return clazz.cast(obj);
    }
    throw new NullPointerException(
        this + " must contain the value " + name + " and it must be a " + clazz);
  }

  /**
   * Get the preference casted to certain class or a default value
   *
   * @param name the name of the preference
   * @param clazz the clazz to which the value of the preference will be casted to
   * @param def the default value in case the preference is null
   * @param <T> the type of the clazz to which the value of the preference will be casted to
   * @return the value of the preference or null if it does not have one
   */
  @NotNull
  default <T> T getValueOr(@NotNull String name, @NotNull Class<T> clazz, @NotNull T def) {
    return Validate.notNullOr(this.getValue(name, clazz), def);
  }

  /**
   * Add a value to the map
   *
   * @param name the name of the value
   * @param value the value to add
   * @return this same instance
   */
  default ValuesMap addValue(@NotNull String name, @NotNull Object value) {
    this.getMap().put(name, value);
    return this;
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   * @return this same instance
   */
  default ValuesMap addValues(@NotNull Map<String, Object> map) {
    map.forEach(this::addValue);
    return this;
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   * @return this same instance
   */
  default ValuesMap addValues(@NotNull ValuesMap map) {
    this.addValues(map.getMap());
    return this;
  }

  /**
   * Removes a value from the map
   *
   * @param key the key inside the map
   */
  default void removeValue(@NotNull String key) {
    this.getMap().remove(key);
  }

  /**
   * The main preferences map to get the preferences from
   *
   * @return the preferences map
   */
  @NotNull
  Map<String, Object> getMap();

  /**
   * Get whether this matches to another map. Matching means that at least one value is the same
   *
   * @param that the other map to check
   * @return true if it matches
   */
  default boolean matches(@NotNull Map<?, ?> that) {
    if (this.getMap().isEmpty() || that.isEmpty()) {
      return false;
    } else {
      Atomic<Boolean> matches = new Atomic<>(false);
      that.forEach(
          (key, value) -> {
            if (key instanceof String
                && ((String) key).startsWith("nickname")
                && value instanceof String
                && this.getMap().containsKey(key)
                && this.getMap().get(key) instanceof String
                && ((String) this.getMap().get(key)).equalsIgnoreCase((String) value)) {
              matches.set(true);
            } else if (key instanceof String
                && this.getMap().containsKey(key)
                && this.getMap().get(key).equals(value)) {
              matches.set(true);
            }
          });
      return matches.get();
    }
  }

  /**
   * Get whether this matches to another map. Matching means that at least one value is the same
   *
   * @param that the other map to check
   * @return true if it matches
   */
  default boolean matches(@NotNull ValuesMap that) {
    return this.matches(that.getMap());
  }
}
