package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.commons.Lots;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;

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
    JsonClient connection = Guido.getClient().getConnection();
    if (connection != null) {
      connection.sendRequest(
          new Request<>(Group[].class, "groups"),
          groups -> {
            List<Group> newGroups = Lots.list(groups);
            this.groups.addAll(newGroups);
            if (consumer != null) {
              consumer.accept(newGroups);
            }
          });
    }
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
