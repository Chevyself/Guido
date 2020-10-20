package com.starfishst.guido.api.data.implementations.data;

import com.starfishst.guido.api.data.ValuesMap;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An implementation for values map */
public class ValuesMapImpl implements ValuesMap {

  /** The map to get values */
  @NotNull private Map<String, Object> map;

  /**
   * Create the values map
   *
   * @param map the initial map
   */
  public ValuesMapImpl(@NotNull Map<String, Object> map) {
    this.map = map;
  }

  /** Create the values map */
  public ValuesMapImpl() {
    this(new HashMap<>());
  }

  @Override
  public @NotNull Map<String, Object> getMap() {
    return this.map;
  }

  @Override
  public boolean matches(@NotNull Map<?, ?> that) {
    return false;
  }

  @Override
  public String toString() {
    return "ValuesMap{" + "map=" + this.map + '}';
  }
}
