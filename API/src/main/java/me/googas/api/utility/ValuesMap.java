package me.googas.api.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  default Object get(@NotNull String key) {
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
  default <T> T get(@NotNull String name, @NotNull Class<T> clazz) {
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
  default <T> T validated(@NotNull String name, @NotNull Class<T> clazz) {
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
  default <T> T getOr(@NotNull String name, @NotNull Class<T> clazz, @NotNull T def) {
    return Validate.notNullOr(this.get(name, clazz), def);
  }

  /**
   * Add a value to the map
   *
   * @param name the name of the value
   * @param value the value to add
   * @return this same instance
   */
  @NotNull
  default ValuesMap put(@NotNull String name, @NotNull Object value) {
    this.getMap().put(name, value);
    return this;
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   * @return this same instance
   */
  default ValuesMap put(@NotNull Map<String, Object> map) {
    map.forEach(this::put);
    return this;
  }

  /**
   * Adds the map values to this map
   *
   * @param map the map to add the values from
   * @return this same instance
   */
  default ValuesMap put(@NotNull ValuesMap map) {
    this.put(map.getMap());
    return this;
  }

  /**
   * Removes a value from the map
   *
   * @param key the key inside the map
   */
  default void remove(@NotNull String key) {
    this.getMap().remove(key);
  }

  /**
   * Get a preference that happens to be a list
   *
   * @param name the name of the preference
   * @param <T> the type of the class
   * @return the list or empty if the preference does not exist
   */
  @SuppressWarnings("unchecked")
  @NotNull
  default <T> List<T> getListValue(@NotNull String name) {
    List<T> list = new ArrayList<>();
    Class<List<T>> aClass = (Class<List<T>>) list.getClass();
    list.addAll(this.getOr(name, aClass, new ArrayList<>()));
    return list;
  }

  /**
   * The main preferences map to get the preferences from
   *
   * @return the preferences map
   */
  @NotNull
  Map<String, Object> getMap();
}
