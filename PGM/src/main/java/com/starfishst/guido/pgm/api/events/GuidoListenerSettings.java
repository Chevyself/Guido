package com.starfishst.guido.pgm.api.events;

import com.starfishst.core.utils.Validate;
import java.util.HashMap;
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
