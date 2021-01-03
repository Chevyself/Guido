package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import java.util.Collection;
import lombok.NonNull;
import me.googas.commons.Lots;

/** An slot inside the tab list */
public interface TabSlot {

  /**
   * Creates a team packet
   *
   * @param manager the protocol manager to create the packet
   * @param action the action for the team packet. 0 = create team; 1 = delete team; 2 = update
   *     team; 3 = add players; 4 = remove players
   * @return the team packet
   */
  @NonNull
  default PacketContainer toTeamPacket(@NonNull ProtocolManager manager, int action) {
    PacketContainer packet = manager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);
    packet.getIntegers().write(1, action);
    packet.getStrings().write(0, this.getTeamName());
    if (action == 0) {
      packet.getStrings().write(1, "");
      packet.getStrings().write(2, "");
      packet.getStrings().write(3, "");
      packet.getStrings().write(4, "always");
      packet.getSpecificModifier(Collection.class).write(0, Lots.list(this.getEntry().getName()));
    } else if (action == 4) {
      packet.getSpecificModifier(Collection.class).write(0, Lots.list(this.getEntry().getName()));
    }
    return packet;
  }

  /**
   * Set the entry of this slot
   *
   * @param entry the new entry for the slot
   */
  void setEntry(@NonNull TabListEntry entry);

  /**
   * Get the index of the slot
   *
   * @return the index of the slot
   */
  int getIndex();

  /**
   * Get the entry allocated in this slot
   *
   * @return the entry from this slot
   */
  @NonNull
  TabListEntry getEntry();

  /**
   * Get the name of the tab team. This is used to organize the entries
   *
   * @return the name of the tab team
   */
  @NonNull
  default String getTeamName() {
    String name =
        (this.getIndex() < 10 ? "0" + this.getIndex() : String.valueOf(this.getIndex()))
            + this.getEntry().getName();
    return name.length() > 16 ? name.substring(0, 16) : name;
  }
}
