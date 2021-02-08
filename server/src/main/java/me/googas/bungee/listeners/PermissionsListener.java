package me.googas.bungee.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.API;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.AbstractPermission;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.bungee.events.GuidoListener;
import me.googas.commons.Lots;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PermissionsListener implements GuidoListener {

  /**
   * Listen for a player joining the game to give it permissions
   *
   * @param event the event of a player joining the game
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPostLoginEvent(PostLoginEvent event) {
    ProxiedPlayer player = event.getPlayer();
    String trim = UUIDUtils.trim(player.getUniqueId());
    Linkable linkable =
        API.getLoader().getLinks().getLink(LinkableType.MINECRAFT, Maps.singleton("uuid", trim));
    if (linkable != null) {
      for (PermissionStack stack :
          Lots.set(linkable.getPermissions("global"), linkable.getPermissions("bungee"))) {
        for (AbstractPermission permission : stack.getPermissions()) {
          if (!permission.isExpired())
            player.setPermission(permission.getNode(), permission.isEnabled());
        }
      }
      for (Group group : this.getGroups(player)) {
        for (PermissionStack stack :
            Lots.set(group.getPermissions("global"), group.getPermissions("bungee"))) {
          for (AbstractPermission permission : stack.getPermissions()) {
            if (!permission.isExpired())
              player.setPermission(permission.getNode(), permission.isEnabled());
          }
        }
      }
    }
  }

  @NonNull
  public List<Group> getGroups(@NonNull ProxiedPlayer player) {
    List<Group> groups = new ArrayList<>();
    Collection<Group> raw = API.getLoader().getGroups().getGroups();
    for (Group group : raw) {
      if (player.hasPermission(this.toPermission(group))) groups.add(group);
    }
    groups.addAll(this.getParents(groups));
    groups.sort(Comparator.comparingInt(Group::getWeight));
    Collections.reverse(groups);
    return groups;
  }

  @NonNull
  public List<Group> getParents(@NonNull List<Group> groups) {
    List<Group> parents = new ArrayList<>();
    for (Group group : groups) {
      parents.addAll(this.getParents(group));
    }
    return parents;
  }

  public List<Group> getParents(@NonNull Group group) {
    ArrayList<Group> parents = new ArrayList<>();
    for (String id : group.getParents()) {
      parents.add(API.getLoader().getGroups().getGroup(id));
    }
    return parents;
  }

  /**
   * Get the group as a permission node
   *
   * @param group the group to getId as a permission node
   * @return the group
   */
  public String toPermission(@NonNull Group group) {
    return "guido.group." + group.getName().replace(" ", "-").toLowerCase();
  }

  public void add(@NonNull ProxiedPlayer player, @Nullable AbstractPermission abstractPermission) {
    if (abstractPermission == null || abstractPermission.isExpired()) return;
    player.setPermission(abstractPermission.getNode(), abstractPermission.isEnabled());
  }

  public void add(
      @NonNull ProxiedPlayer player, @Nullable Collection<AbstractPermission> abstractPermissions) {
    if (abstractPermissions == null) return;
    for (AbstractPermission abstractPermission : abstractPermissions) {
      this.add(player, abstractPermission);
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "permissions";
  }
}
