package com.starfishst.bukkit.dependencies.protocol.tab;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;

/** An entry that can be replaced with a player entry */
public class TabListPlayerEmptyEntry extends TabListEmptyEntry {

  /**
   * Checks whether this entry can be replaced with a player
   *
   * @param player the player to check if it can replace
   * @return true if it can be replaced
   */
  public boolean canBeReplaced(@NonNull OfflinePlayer player) {
    return true;
  }
}
