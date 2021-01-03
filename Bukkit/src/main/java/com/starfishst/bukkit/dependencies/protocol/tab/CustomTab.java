package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.starfishst.bukkit.api.Guido;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Lots;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/** The custom tab for a player */
public class CustomTab implements Comparator<TabSlot> {

  /** The protocol manager to send packets */
  @NonNull
  private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

  /** The owner of this tab */
  @NonNull @Getter private final UUID player;

  /** The slots of the tab list */
  @NonNull @Getter private final List<TabSlot> slots;

  public CustomTab(@NonNull UUID player, @NonNull List<TabSlot> slots) {
    this.player = player;
    this.slots = slots;
  }

  /**
   * Get the slot that contains an entry with certain uuid
   *
   * @param uuid the uuid of the entry to match
   * @return the slot if there is one
   */
  @Nullable
  public TabSlot getSlot(@NonNull UUID uuid) {
    for (TabSlot slot : this.slots) {
      if (slot.getEntry().getUUID().equals(uuid)) {
        return slot;
      }
    }
    return null;
  }

  /**
   * Get an entry using its uuid
   *
   * @param uuid the uuid of the entry
   * @return the entry
   */
  @Nullable
  public TabListEntry getEntry(@NonNull UUID uuid) {
    TabSlot slot = this.getSlot(uuid);
    if (slot != null) {
      return slot.getEntry();
    }
    return null;
  }

  /**
   * Get the slots that are in certain index
   *
   * @param index the index to get the
   * @return the slots in the index
   */
  @NonNull
  public List<TabSlot> getSlots(int index) {
    List<TabSlot> slots = new ArrayList<>();
    for (TabSlot slot : this.slots) {
      if (slot.getIndex() == index) {
        slots.add(slot);
      }
    }
    return slots;
  }

  /**
   * Get an empty slot for the player
   *
   * @param player the player to add
   * @return the empty slot found for the player
   */
  @Nullable
  public TabSlotPlayer getEmptySlot(@NonNull OfflinePlayer player) {
    for (TabSlot slot : this.slots) {
      if (slot instanceof TabSlotPlayer && ((TabSlotPlayer) slot).canContain(player)) {
        return (TabSlotPlayer) slot;
      }
    }
    return null;
  }

  /**
   * Create a player info packet with certain action and data
   *
   * @param action the action required for the packet
   * @param data the data to append in the packet
   * @return the packet
   */
  @NonNull
  public PacketContainer createPlayerInfoPacket(
      @NonNull EnumWrappers.PlayerInfoAction action, @NonNull List<PlayerInfoData> data) {
    PacketContainer packet =
        CustomTab.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
    packet.getModifier().writeDefaults();
    packet.getPlayerInfoAction().write(0, action);
    packet.getPlayerInfoDataLists().write(0, data);
    return packet;
  }

  /**
   * Send a team packet
   *
   * @param action the action for the team packet
   */
  public void sendTeamPacket(int action) {
    Player player = this.bukkitPlayer();
    if (player != null) {
      for (PacketContainer packet : this.toTeamPacket(action)) {
        try {
          CustomTab.protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Get the player info data that matches the other data to match
   *
   * @param toMatch the data to match
   * @return the player data that matched
   * @param addAll whether to add all the data from every slot
   */
  @NonNull
  public List<PlayerInfoData> toPlayerInfoData(
      @NonNull List<PlayerInfoData> toMatch, boolean addAll) {
    List<PlayerInfoData> matches = new ArrayList<>();
    List<TabSlot> added = new ArrayList<>();
    for (PlayerInfoData playerData : toMatch) {
      TabSlot slot = this.getSlot(playerData.getProfile().getUUID());
      if (slot != null) {
        matches.add(slot.getEntry().toPlayerInfoData(this));
        added.add(slot);
      } else {
        Player player = Bukkit.getPlayer(playerData.getProfile().getUUID());
        if (this.listener().isJoining(playerData) && player != null) {
          TabListPlayerEntry entry = new TabListPlayerEntry(player);
          matches.add(entry.toPlayerInfoData(this));
        } else {
          matches.add(playerData);
        }
      }
    }
    if (addAll) {
      for (TabSlot slot : this.slots) {
        if (!added.contains(slot)) {
          matches.add(slot.getEntry().toPlayerInfoData(this));
        }
      }
    }
    return matches;
  }

  /**
   * Get the player info data from the slots
   *
   * @return the data
   */
  @NonNull
  public List<PlayerInfoData> toPlayerInfoData() {
    List<PlayerInfoData> data = new ArrayList<>();
    for (TabSlot slot : this.slots) {
      data.add(slot.getEntry().toPlayerInfoData(this));
    }
    return data;
  }

  /**
   * Create a team packet for every entry
   *
   * @param action the action to archive see {@link TabSlot#toTeamPacket(ProtocolManager, int)}
   * @return the created team packets
   */
  @NonNull
  public List<PacketContainer> toTeamPacket(int action) {
    List<PacketContainer> packets = new ArrayList<>();
    for (TabSlot slot : this.slots) {
      packets.add(slot.toTeamPacket(CustomTab.protocolManager, action));
    }
    return packets;
  }

  /** Closes this custom tab */
  public void exit() {
    // TODO
  }

  /**
   * Removes the players from a player info data list
   *
   * @param data the data to get the unique ids of the players
   */
  public void removePlayersFromData(List<PlayerInfoData> data) {
    List<UUID> list = new ArrayList<>();
    for (PlayerInfoData info : data) {
      list.add(info.getProfile().getUUID());
    }
    this.removePlayers(list);
  }

  /**
   * Removes the players inside the list from the tab
   *
   * @param list the list of the players uuid
   */
  public void removePlayers(List<UUID> list) {
    HashMap<TabSlot, TabListEntry> toSet = new HashMap<>();
    for (UUID uuid : list) {
      TabSlot slot = this.getSlot(uuid);
      if (slot instanceof TabSlotPlayer && slot.getEntry() instanceof TabListPlayerEntry) {
        toSet.put(slot, ((TabSlotPlayer) slot).getReplacement());
      }
    }
    this.setEntries(toSet);
  }

  /**
   * Updates the display name of a placeholder entry
   *
   * @param entry the entry to update
   */
  public void updatePlaceholderEntry(@NonNull TabListPlaceholderEntry entry) {
    this.updatePlaceholderEntries(Lots.list(entry));
  }

  /**
   * Updates the display name of a list of placeholders entries
   *
   * @param entries the entries to update
   */
  public void updatePlaceholderEntries(@NonNull List<TabListPlaceholderEntry> entries) {
    Player player = this.bukkitPlayer();
    if (player != null) {
      List<PlayerInfoData> toSend = new ArrayList<>();
      for (TabListPlaceholderEntry entry : entries) {
        toSend.add(entry.toPlayerInfoData(this));
      }
      try {
        CustomTab.protocolManager.sendServerPacket(
            player,
            this.createPlayerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, toSend));
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  @Nullable
  private Player bukkitPlayer() {
    return Bukkit.getPlayer(this.player);
  }

  @NonNull
  public OfflinePlayer bukkitOfflinePlayer() {
    return Bukkit.getOfflinePlayer(this.player);
  }

  @NonNull
  public TabListListener listener() {
    return Guido.getListener(TabListListener.class);
  }

  /**
   * Set the entries in certain slots
   *
   * @param entries the entries to set in the slots
   */
  public void setEntries(@NonNull HashMap<? extends TabSlot, ? extends TabListEntry> entries) {
    Player player = this.bukkitPlayer();
    List<PlayerInfoData> toRemove = new ArrayList<>();
    List<PlayerInfoData> toAdd = new ArrayList<>();
    List<PacketContainer> deleteTeam = new ArrayList<>();
    List<PacketContainer> createTeam = new ArrayList<>();
    for (TabSlot slot : entries.keySet()) {
      TabListEntry newEntry = entries.get(slot);
      toRemove.add(slot.getEntry().toPlayerInfoData(this));
      toAdd.add(newEntry.toPlayerInfoData(this));
      deleteTeam.add(slot.toTeamPacket(CustomTab.protocolManager, 1));
      slot.setEntry(newEntry);
      createTeam.add(slot.toTeamPacket(CustomTab.protocolManager, 0));
    }
    if (player != null) {
      List<PacketContainer> toSend = new ArrayList<>();
      toSend.add(
          this.createPlayerInfoPacket(
              EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, toRemove)); // Remove the entries
      toSend.add(
          this.createPlayerInfoPacket(
              EnumWrappers.PlayerInfoAction.ADD_PLAYER, toAdd)); // Adds the entries
      toSend.addAll(deleteTeam); // Removes the entries from the teams
      toSend.addAll(createTeam); // Adds the entries to the teams
      for (PacketContainer packet : toSend) {
        try {
          CustomTab.protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Set the entries in certain slots using their index
   *
   * @param entries the entries to add
   * @param <T> the type of entries
   */
  @Deprecated
  public <T extends TabListEntry> void setEntriesByIndex(@NonNull HashMap<Integer, T> entries) {
    HashMap<TabSlot, T> toAdd = new HashMap<>();
    for (Integer integer : entries.keySet()) {
      T entry = entries.get(integer);
      TabSlot slot = this.getSlot(entry.getUUID());
      if (slot != null) {
        toAdd.put(slot, entry);
      }
    }
    this.setEntries(toAdd);
  }

  @Override
  public int compare(@NonNull TabSlot tabSlot, @NonNull TabSlot comparison) {
    return Integer.compare(tabSlot.getIndex(), comparison.getIndex());
  }
}
