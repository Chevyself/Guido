package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.utils.BukkitUtils;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

/**
 * Listener for the decorations in a players name
 */
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
            Collection<Group> groups = permissions.getGroups(event.getPlayer().getUniqueId());
            event.getPlayer().setDisplayName(this.getPrefixex(groups) + event.getPlayer().getName() + this.getSuffixes(groups));
        }
    }

    /**
     * Get the prefixes for the groups of a player
     *
     * @param groups the groups of the player
     * @return the prefixes for the given groups
     */
    @NotNull
    public String getPrefixex(@NotNull Collection<Group> groups) {
        int currentWeight = 0;
        StringBuilder builder = Strings.getBuilder();
        for (Group group : groups) {
            if (group.getWeight() < currentWeight || group.getWeight() == currentWeight) {
                currentWeight = group.getWeight();
                builder.append(group.getPreferences().getValueOr("prefix", String.class, ""));
            }
        }
        return BukkitUtils.build(builder.toString());
    }

    /**
     * Get the suffixes for the groups of a player
     *
     * @param groups the groups of the player
     * @return the suffixes for the given groups
     */
    @NotNull
    public String getSuffixes(@NotNull Collection<Group> groups) {
        int currentWeight = 0;
        StringBuilder builder = Strings.getBuilder();
        for (Group group : groups) {
            if (group.getWeight() < currentWeight || group.getWeight() == currentWeight) {
                currentWeight = group.getWeight();
                builder.append(group.getPreferences().getValueOr("suffix", String.class, ""));
            }
        }
        return BukkitUtils.build(builder.toString());
    }

    /**
     * Called on {@link #unregister()}
     */
    @Override
    public void onUnload() {

    }

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
