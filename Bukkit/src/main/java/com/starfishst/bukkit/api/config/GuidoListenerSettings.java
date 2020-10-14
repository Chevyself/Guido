package com.starfishst.bukkit.api.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  @Nullable
  default <T> T getSetting(@NotNull String name, @NotNull Class<T> clazz) {
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
   * @param clazz the class that the setting is made of
   * @param <T> the type of the class
   * @return the list or empty if the setting does not exist
   */
  @SuppressWarnings("unchecked")
  @NotNull
  default <T> List<T> getListSetting(@NotNull String name, @NotNull Class<T> clazz) {
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
  @NotNull
  default <T> T getSettingOr(@NotNull String name, @NotNull Class<T> clazz, @NotNull T def) {
    return Validate.notNullOr(this.getSetting(name, clazz), def);
  }

  /**
   * Get the name of the listener to which this are settings
   *
   * @return the name of the listener
   */
  @NotNull
  String getName();

  /**
   * Get the setting of this listener
   *
   * @return the settings of this listener
   */
  @NotNull
  HashMap<String, Object> getSettings();
}
