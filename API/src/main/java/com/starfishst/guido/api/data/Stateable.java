package com.starfishst.guido.api.data;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** This object represents an entity that can have stats */
public interface Stateable {

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @NotNull
  HashMap<String, Double> getStats();
}
