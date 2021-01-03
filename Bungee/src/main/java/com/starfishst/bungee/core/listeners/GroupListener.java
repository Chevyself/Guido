package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.client.requests.BungeeRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.commons.Lots;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Handles groups used for permissions */
public class GroupListener implements GuidoListener {

  /** The list of loaded groups */
  @NonNull private final List<Group> groups = new ArrayList<>();

  /**
   * Loads new groups requesting them to the bot
   *
   * @param consumer what ever you would like to do with the new loaded groups
   */
  public void loadGroups(Consumer<List<Group>> consumer) {
    new BungeeRequest<>(Group[].class, "groups")
        .send(
            groups -> {
              List<Group> newGroups = Lots.list(groups);
              this.groups.addAll(newGroups);
              if (consumer != null) {
                consumer.accept(newGroups);
              }
            });
  }

  /** Clears the list of loaded groups */
  public void unloadGroups() {
    this.groups.clear();
  }

  /**
   * Reloads the groups
   *
   * @param consumer what ever to do with the new loaded groups
   */
  public void reload(Consumer<List<Group>> consumer) {
    this.unloadGroups();
    this.loadGroups(consumer);
  }

  /**
   * Get a group by its permission given in {@link #toPermission(Group)}
   *
   * @param permission the permission to match
   * @return the group if matches null otherwise
   * @deprecated since BETA-4 no longer used
   */
  public Group getGroupByPermission(@NonNull String permission) {
    for (Group group : this.groups) {
      if (this.toPermission(group).equalsIgnoreCase(permission)) {
        return group;
      }
    }
    return null;
  }

  /**
   * Get the group as a permission node
   *
   * @param group the group to get as a permission node
   * @return the group
   */
  public String toPermission(@NonNull Group group) {
    return "guido.group." + group.getName().replace(" ", "-");
  }

  /**
   * Get the list of parents of the group
   *
   * @param group the group to get the parents
   * @return the list of groups
   */
  public List<Group> getParents(@NonNull Group group) {
    ArrayList<Group> parents = new ArrayList<>();
    for (String id : group.getParents()) {
      parents.add(this.getGroup(id));
    }
    return parents;
  }

  /**
   * Get a group by its id
   *
   * @param id the id to match
   * @return the group if the id matches else null
   */
  private Group getGroup(String id) {
    for (Group group : this.groups) {
      if (group.getId().equals(id)) return group;
    }
    return null;
  }

  @NonNull
  public List<Group> getGroups(@NonNull ProxiedPlayer player, boolean sorted) {
    List<Group> groups = new ArrayList<>();
    for (Group group : this.groups) {
      if (player.hasPermission(this.toPermission(group))) groups.add(group);
    }
    if (sorted) {
      groups.sort(Comparator.comparingInt(Group::getWeight));
      Collections.reverse(groups);
    }
    return groups;
  }

  public List<Group> getParents(@NonNull List<Group> groups) {
    List<Group> parents = new ArrayList<>();
    if (groups.isEmpty()) return parents;
    for (Group group : groups) {
      parents.addAll(this.getParents(group));
    }
    return parents;
  }

  /**
   * Get the collection of loaded groups
   *
   * @return the collection of loaded groups
   */
  public Collection<Group> getGroups() {
    return this.groups;
  }

  @Override
  public void onUnload() {
    this.unloadGroups();
  }

  @Override
  public @NonNull String getName() {
    return "groups";
  }
}
