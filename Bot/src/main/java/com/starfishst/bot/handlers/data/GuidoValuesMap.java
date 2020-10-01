package com.starfishst.bot.handlers.data;

import com.starfishst.guido.api.data.ValuesMap;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An implementation of {@link ValuesMap} */
public class GuidoValuesMap implements ValuesMap {

  /** The map of values */
  @NotNull private final HashMap<String, Object> map;

  /**
   * Create the values map
   *
   * @param map the map of values
   */
  public GuidoValuesMap(@NotNull HashMap<String, Object> map) {
    this.map = map;
  }

  /** Create the values map */
  public GuidoValuesMap() {
    this(new HashMap<>());
  }

  @Override
  public @NotNull Map<String, Object> getMap() {
    return this.map;
  }
}
