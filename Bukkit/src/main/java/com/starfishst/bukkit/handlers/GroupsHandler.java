package com.starfishst.bukkit.handlers;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.dependencies.pgm.listeners.groups.PGMGroupsHandler;
import com.starfishst.bukkit.util.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.permissions.Group;
import me.googas.commons.Lots;
import me.googas.messaging.json.client.JsonClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/** Listens for groups */
public class GroupsHandler implements Handler {

  /** The list of loaded groups */
  @NonNull private final List<Group> groups = new ArrayList<>();

  /**
   * Loads new groups requesting them to the bot
   *
   * @param consumer what ever you would like to do with the new loaded groups
   */
  public void loadGroups(Consumer<List<Group>> consumer) {
    JsonClient connection = Guido.getClient().getConnection();
    Requests.Groups.getGroups()
        .send(
            connection,
            optional ->
                optional.ifPresent(
                    groups -> {
                      List<Group> newGroups = Lots.list(groups);
                      this.groups.addAll(newGroups);
                      if (consumer != null) {
                        consumer.accept(newGroups);
                      }
                      if (Guido.isPPGMConnected()) {
                        this.PGM().addInPGM(Lots.set(groups));
                        return;
                      }
                      // Registers the groups in Bukkit
                      for (Group group : this.groups) {
                        Bukkit.getPluginManager().addPermission(this.toPermission(group));
                      }
                    }));
  }

  /**
   * Get the node of a context
   *
   * @param group the group that will be appended in the node
   * @return the node of the permission
   */
  @NonNull
  public String getNode(@NonNull Group group) {
    return "guido.group." + group.getName().toLowerCase().replace(" ", "-");
  }

  /** Clears the list of loaded groups */
  public void unloadGroups() {
    if (!this.groups.isEmpty()) {
      for (Group group : this.groups) {
        Bukkit.getServer().getPluginManager().removePermission(this.getNode(group));
      }
      if (Guido.isPPGMConnected()) this.PGM().unloadGroups(this.groups);
    }
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

  public boolean isDefault(@NonNull Group group) {
    return group.getPreferences().getOr("default", Boolean.class, false);
  }

  /**
   * Get the group as a permission node
   *
   * @param group the group to get as a permission node
   * @return the group
   */
  public Permission toPermission(@NonNull Group group) {
    return new Permission(
        this.getNode(group),
        this.isDefault(group) ? PermissionDefault.TRUE : PermissionDefault.FALSE,
        Permissions.getChildren(
            group, this.getParents(group), Guido.getConfiguration().getContext()));
  }

  @NonNull
  public List<Group> getGroups(@NonNull Player player) {
    List<Group> groups = new ArrayList<>();
    for (Group group : this.groups) {
      if (player.hasPermission(this.getNode(group))) groups.add(group);
    }
    return groups;
  }

  @NonNull
  private PGMGroupsHandler PGM() {
    return Guido.getHandlerRegistry().requireHandler(PGMGroupsHandler.class);
  }

  @Override
  public @NonNull String getName() {
    return "groups";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
