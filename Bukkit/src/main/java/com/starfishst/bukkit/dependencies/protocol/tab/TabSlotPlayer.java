package com.starfishst.bukkit.dependencies.protocol.tab;

import lombok.NonNull;
import me.googas.commons.Strings;
import me.googas.commons.builder.ToStringBuilder;
import org.bukkit.OfflinePlayer;

/** A slot that can have a player inside of it */
public class TabSlotPlayer implements TabSlot {

  /** The index of the slot */
  private final int index;

  /** The tab entry of the slot */
  @NonNull private TabListEntry entry;

  /**
   * Create the slot for the player
   *
   * @param index the index of the slot
   * @param entry the entry for the slot
   */
  public TabSlotPlayer(int index, @NonNull TabListEntry entry) {
    this.index = index;
    this.entry = entry;
  }

  /**
   * Checks if this slot can contain a player
   *
   * @param player the player to contain
   * @return true if it can
   */
  public boolean canContain(@NonNull OfflinePlayer player) {
    if (this.entry instanceof TabListPlayerEmptyEntry) {
      return ((TabListPlayerEmptyEntry) this.entry).canBeReplaced(player);
    }
    return false;
  }

  /**
   * Get the replacement when a player has to be removed
   *
   * @return the entry that will replace the removed player
   */
  @NonNull
  public TabListEntry getReplacement() {
    return new TabListPlayerEmptyEntry();
  }

  /**
   * Get the index of the slot
   *
   * @return the index of the slot
   */
  @Override
  public int getIndex() {
    return this.index;
  }

  /**
   * Get the entry allocated in this slot
   *
   * @return the entry from this slot
   */
  @Override
  public @NonNull TabListEntry getEntry() {
    return this.entry;
  }

  /**
   * Set the entry of this slot
   *
   * @param entry the new entry for the slot
   */
  @Override
  public void setEntry(@NonNull TabListEntry entry) {
    this.entry = entry;
  }

  /**
   * Get the name of the tab team. This is used to organize the entries
   *
   * @return the name of the tab team
   */
  @Override
  public @NonNull String getTeamName() {
    String name;
    if (this.entry instanceof TabListPlayerEntry) {
      name = Strings.getBuilder().append(TabSlot.super.getTeamName()).insert(2, "0").toString();
    } else {
      name = Strings.getBuilder().append(TabSlot.super.getTeamName()).insert(2, "_").toString();
    }
    return name.length() > 16 ? name.substring(0, 16) : name;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("index", this.index)
        .append("entry", this.entry)
        .build();
  }
}
