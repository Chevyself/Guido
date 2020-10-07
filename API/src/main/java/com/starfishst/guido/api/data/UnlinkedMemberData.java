package com.starfishst.guido.api.data;

import com.starfishst.core.utils.cache.ICatchable;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** An unlinked user is a {@link } */
public interface UnlinkedMemberData extends MemberData, ICatchable {

  /** Deletes this unlinked member */
  void delete();

  /**
   * Get the key that is linked
   *
   * @return the key
   */
  @NotNull
  String getKey();

  /**
   * Get the value that is linked
   *
   * @return the value
   */
  @NotNull
  String getValue();

  @Override
  default long getId() {
    return -1;
  }

  @Override
  long getGuildId();

  @Override
  @NotNull
  HashMap<String, Double> getStats();

  @Override
  default void addLink(@NotNull String key, @NotNull String value) {
    throw new UnsupportedOperationException("Unlinked members cannot have links");
  }

  @Override
  default void removeLink(@NotNull String key) {
    throw new UnsupportedOperationException("Unlinked members cannot have links");
  }

  @Override
  @NotNull
  default HashMap<String, String> getLinks() {
    throw new UnsupportedOperationException("Unlinked members cannot have links");
  }
}
