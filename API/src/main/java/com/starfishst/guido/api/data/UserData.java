package com.starfishst.guido.api.data;

import com.starfishst.guido.api.data.lang.Localizable;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/** The data of a discord user not required to be in a guild */
public interface UserData extends ICatchable, Localizable {

  /**
   * Get the unique id of the user
   *
   * @return the unique id of the user
   */
  @NotNull
  String getId();

  @Override
  default void setLang(@NotNull String lang) {
    this.getPreferences().addValue("lang", lang);
  }

  /**
   * Get the preferences of the user
   *
   * @return the preferences of the user
   */
  @NotNull
  ValuesMap getPreferences();

  @Override
  default @NotNull String getLang() {
    return this.getPreferences().getValueOr("lang", String.class, "en");
  }
}
