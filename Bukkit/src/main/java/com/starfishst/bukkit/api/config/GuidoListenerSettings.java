package com.starfishst.bukkit.api.config;

import lombok.NonNull;
import me.googas.api.ValuesMap;

/** The settings for a listener */
public interface GuidoListenerSettings extends ValuesMap {

  /**
   * Get the name of the listener to which this are settings
   *
   * @return the name of the listener
   */
  @NonNull
  String getName();
}
