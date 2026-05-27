package com.starfishst.bukkit.commands;

import lombok.NonNull;
import me.googas.starbox.modules.Module;

/** A command of guido */
public interface GuidoCommand extends Module {

  /**
   * Set whether the command is enabled
   *
   * @param bol the new value
   */
  default void setEnabled(boolean bol) {}

  /**
   * Get the name of the command. This is used to enable it or not from the configuration
   *
   * @return the name of the command
   */
  @NonNull
  String getName();

  /**
   * Get whether the command is enabled
   *
   * @return true if the command is enabled
   */
  default boolean isEnabled() {
    return false;
  }
}
