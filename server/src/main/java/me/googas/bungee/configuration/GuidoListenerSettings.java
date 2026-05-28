package me.googas.bungee.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/** The settings for a listener */
public interface GuidoListenerSettings {

  /**
   * Get the setting casted to certain class or null if the setting is not inside the map
   *
   * @param name the name of the setting
   * @param clazz the clazz to which the value of the setting will be casted to
   * @param <T> the type of the clazz to which the value of the setting will be casted to
   * @return the value of the setting or null if it does not have one
   */
  default <T> T getSetting(@NonNull String name, @NonNull Class<T> clazz) {
    Object obj = this.getSettings().get(name);
    if (obj != null) {
      return clazz.cast(obj);
    } else {
      return null;
    }
  }

  /**
   * Get a setting that happens to be a list
   *
   * @param name the name of the setting
   * @param <T> the type of the class
   * @return the list or empty if the setting does not exist
   */
  @SuppressWarnings("unchecked")
  @NonNull
  default <T> List<T> getListSetting(@NonNull String name) {
    List<T> list = new ArrayList<>();
    Class<List<T>> aClass = (Class<List<T>>) list.getClass();
    list.addAll(this.getSettingOr(name, aClass, new ArrayList<>()));
    return list;
  }

  /**
   * Get the setting casted to certain class or a default value
   *
   * @param name the name of the setting
   * @param clazz the clazz to which the value of the setting will be casted to
   * @param def the default value in case the setting is null
   * @param <T> the type of the clazz to which the value of the setting will be casted to
   * @return the value of the setting or null if it does not have one
   */
  @NonNull
  default <T> T getSettingOr(@NonNull String name, @NonNull Class<T> clazz, @NonNull T def) {
    T setting = this.getSetting(name, clazz);
    return setting != null ? setting : def;
  }

  /**
   * Get the name of the listener to which this are settings
   *
   * @return the name of the listener
   */
  @NonNull
  String getName();

  /**
   * Get the setting of this listener
   *
   * @return the settings of this listener
   */
  @NonNull
  Map<String, Object> getSettings();
}
