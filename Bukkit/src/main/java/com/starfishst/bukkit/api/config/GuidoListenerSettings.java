package com.starfishst.bukkit.api.config;

import me.googas.api.utility.ValuesMap;
import org.jetbrains.annotations.NotNull;

/** The settings for a listener */
public interface GuidoListenerSettings extends ValuesMap {

  /**
   * Get the name of the listener to which this are settings
   *
   * @return the name of the listener
   */
  @NotNull
  String getName();
}
