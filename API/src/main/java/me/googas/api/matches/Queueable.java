package me.googas.api.matches;

import lombok.NonNull;
import me.googas.api.utility.Stateable;

/** This object represents entity that can be inside a queue */
public interface Queueable extends Stateable {

  /**
   * Get as a single way to identify it. For example in the case of discord it will be the tag, for
   * minecraft its nickname and if its a team it's name
   *
   * @return a simple way to identify the data
   */
  @NonNull
  String getSingle();
}
