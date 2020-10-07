package com.starfishst.guido.api.data.links;

import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Stateable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents data that has been linked to an user */
public interface LinkedData extends Permissible, Stateable {

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @NotNull
  LinkedDataType getType();

  /**
   * Get the id of the linked user. If this returns null it must be because the data has not been
   * linked yet
   *
   * @return the id of the linked user
   */
  @Nullable
  String getLinkedId();
}
