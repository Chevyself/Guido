package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.ArrayList;
import java.util.List;
import me.googas.api.permissions.Group;
import me.googas.commons.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

/** Listener for the decorations in a players name */
public class DecorationsListener implements GuidoListener {

  /**
   * Listen to a player joining the game to give them the prefixes
   *
   * @param event the event of a player login in the game
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLoginEvent(PlayerLoginEvent event) {
    PermissionListener permissions = Guido.getListener(PermissionListener.class);
    if (permissions != null) {
      List<Group> groups = new ArrayList<>(permissions.getGroups(event.getPlayer().getUniqueId()));
      Guido.getLogger().info("Groups of " + event.getPlayer().getUniqueId() + " " + groups);
      String displayName =
          BukkitUtils.build(
              this.getPrefixes(groups)
                  + event.getPlayer().getName()
                  + this.getSuffixes(groups)
                  + "&r");
      Guido.getLogger()
          .info("Display name of " + event.getPlayer().getUniqueId() + " " + displayName);
      event.getPlayer().setDisplayName(displayName);
    }
  }

  /**
   * Get the prefixes for the groups of a player
   *
   * @param groups the groups of the player
   * @return the prefixes for the given groups
   */
  @NotNull
  public String getPrefixes(@NotNull List<Group> groups) {
    int currentWeight = this.getMinWeight(groups);
    StringBuilder builder = Strings.getBuilder();
    for (Group group : groups) {
      if (group.getWeight() < currentWeight || group.getWeight() == currentWeight) {
        currentWeight = group.getWeight();
        builder.append(group.getPreferences().getOr("prefix", String.class, ""));
      }
    }
    return builder.toString();
  }

  /**
   * Get the suffixes for the groups of a player
   *
   * @param groups the groups of the player
   * @return the suffixes for the given groups
   */
  @NotNull
  public String getSuffixes(@NotNull List<Group> groups) {
    int currentWeight = this.getMinWeight(groups);
    StringBuilder builder = Strings.getBuilder();
    for (Group group : groups) {
      if (group.getWeight() < currentWeight || group.getWeight() == currentWeight) {
        currentWeight = group.getWeight();
        builder.append(group.getPreferences().getOr("suffix", String.class, ""));
      }
    }
    return BukkitUtils.build(builder.toString());
  }

  /**
   * Get the minimum weight from a list of groups
   *
   * @param groups the groups to get the minimum weight
   * @return the minimum weight and 0 if there is no groups
   */
  private int getMinWeight(@NotNull List<Group> groups) {
    if (groups.isEmpty()) {
      return 0;
    } else {
      int min = groups.get(0).getWeight();
      for (Group group : groups) {
        if (group.getWeight() < min) {
          min = group.getWeight();
        }
      }
      return min;
    }
  }

  /** Called on {@link #unregister()} */
  @Override
  public void onUnload() {}

  /**
   * Get the name of this listener
   *
   * @return the name of the listener
   */
  @Override
  public @NotNull String getName() {
    return "decorations";
  }
}
