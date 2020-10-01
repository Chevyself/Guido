package com.starfishst.guido.api.data;

import com.starfishst.core.utils.cache.ICatchable;
import com.starfishst.guido.api.data.lang.Localizable;
import org.jetbrains.annotations.NotNull;

/** The data of a discord user not required to be in a guild */
public interface UserData extends ICatchable, Localizable {

  /**
   * Get the unique id of the discord user
   *
   * @return the unique id of the discord user
   */
  long getId();

  @Override
  default void setLang(@NotNull String lang) {
    this.getPreferences().addValue("lang", lang);
  }

  /**
   * Get the preferences of an user
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
