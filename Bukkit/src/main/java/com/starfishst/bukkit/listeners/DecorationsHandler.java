package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.List;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.commons.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

/** Listener for the decorations in a players name */
public class DecorationsHandler implements Handler {

  /**
   * Listen to a player joining the game to give them the prefixes
   *
   * @param event the event of a player login in the game
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLoginEvent(PlayerLoginEvent event) {
    Player player = event.getPlayer();
    List<Group> groups = this.groups().getGroups(player);
    String displayName =
        BukkitUtils.build(
            this.getPrefixes(groups) + player.getName() + this.getSuffixes(groups) + "&r");
    player.setDisplayName(displayName);
  }

  /**
   * Get the prefixes for the groups of a player
   *
   * @param groups the groups of the player
   * @return the prefixes for the given groups
   */
  @NonNull
  public String getPrefixes(@NonNull List<Group> groups) {
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
  @NonNull
  public String getSuffixes(@NonNull List<Group> groups) {
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
  private int getMinWeight(@NonNull List<Group> groups) {
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

  @NonNull
  private GroupsHandler groups() {
    return Guido.getHandlerRegistry().requireHandler(GroupsHandler.class);
  }

  @Override
  public @NonNull String getName() {
    return "decorations";
  }
}
