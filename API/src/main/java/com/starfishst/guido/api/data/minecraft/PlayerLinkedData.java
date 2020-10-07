package com.starfishst.guido.api.data.minecraft;

import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/** The linked data for a minecraft player */
public interface PlayerLinkedData extends ICatchable, LinkedData {

  /**
   * Get the trimmed unique id of the minecraft player
   *
   * @return the trimmed unique id
   */
  @NotNull
  String getTrimmedUniqueId();

  /**
   * Get the built unique id of the player
   *
   * @return the built trimmed unique id
   */
  @NotNull
  default UUID getUniqudeId() {
    return UUIDUtils.untrim(this.getTrimmedUniqueId());
  }

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @Override
  @NotNull
  default LinkedDataType getType() {
    return LinkedDataType.MINECRAFT;
  }
}
