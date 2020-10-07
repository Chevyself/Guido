package com.starfishst.guido.api.data.discord;

import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/** The linked data for a member inside a guild */
public interface MemberLinkedData extends ICatchable, LinkedData {

  /**
   * Get the unique id of the discord user
   *
   * @return the unique id of the discord user
   */
  long getId();

  /**
   * Get the unique id where this entity is a member
   *
   * @return the unique id of the guild
   */
  long getGuildId();

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @Override
  @NotNull
  default LinkedDataType getType() {
    return LinkedDataType.DISCORD_GUILD;
  }
}
