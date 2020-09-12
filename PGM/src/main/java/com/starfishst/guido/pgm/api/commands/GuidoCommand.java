package com.starfishst.guido.pgm.api.commands;

import org.jetbrains.annotations.NotNull;

/** A command of guido */
public interface GuidoCommand {

  /**
   * Set whether the command is enabled
   *
   * @param bol the new value
   */
  void setEnabled(boolean bol);

  /**
   * Get the name of the command. This is used to enable it or not from the configuration
   *
   * @return the name of the command
   */
  @NotNull
  String getName();

  /**
   * Get whether the command is enabled
   *
   * @return true if the command is enabled
   */
  boolean isEnabled();
}
