package me.googas.api.matches.queue;

import lombok.NonNull;
import me.googas.api.Stateable;

/** This object represents entity that can be inside a queue */
public interface Queueable extends Stateable {

  /**
   * Get as a single way to identify it. For example in the case of discord it will be the tag, for
   * Minecraft its nickname and if it's a team its name
   *
   * @return a simple way to identify the data
   */
  @NonNull
  String getSingle();
}
