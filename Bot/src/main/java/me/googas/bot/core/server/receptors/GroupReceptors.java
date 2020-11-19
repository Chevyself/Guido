package me.googas.bot.core.server.receptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoGroup;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors for groups */
public class GroupReceptors {

  /**
   * The receptors to get a group
   *
   * @param id the id of the group to get
   * @return the group if found or null
   */
  @Receptor("group")
  public BotGroup group(@ParamName("id") String id) {
    return Guido.getDataLoader().getGroup(id);
  }

  /**
   * The receptor to get all the groups
   *
   * @return the existing groups
   */
  @Receptor("groups")
  public Collection<Group> groups() {
    return Guido.getDataLoader().getGroups();
  }

  /**
   * Create a group and get an id for it
   *
   * @param name the name of the group
   * @param weight the weight of the group
   * @return the id of the group created
   */
  @Receptor("create-group")
  public String createGroup(@ParamName("name") String name, @ParamName("weight") int weight) {
    return new GuidoGroup(weight, new GuidoValuesMap(), new HashSet<>(), name, new ArrayList<>())
        .cache()
        .getId();
  }

  /**
   * Deletes a group with the given id
   *
   * @param id the id to match
   * @return true if the group is deleted
   */
  @Receptor("delete-group")
  public boolean delete(@ParamName("id") String id) {
    return Guido.getDataLoader().deleteGroup(id);
  }

  /**
   * Updates the name and weight of a group
   *
   * @param id the id of the group
   * @param name the new name of the group
   * @param weight the new weight of the group
   * @return whether the group was updated
   */
  @Receptor("group-update")
  public boolean update(
      @ParamName("id") String id, @ParamName("name") String name, @ParamName("weight") int weight) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      group.setName(name);
      group.setWeight(weight);
      return true;
    }
    return false;
  }

  /**
   * Set a preference
   *
   * @param groupId the id of the group to set the preference to
   * @param key the key of the preference
   * @param value the value of the preference
   * @return whether the preference was set
   */
  @Receptor("group-set-preference")
  public boolean setPreference(
      @ParamName("id") String groupId,
      @ParamName("key") String key,
      @ParamName("value") Object value) {
    BotGroup group = Guido.getDataLoader().getGroup(groupId);
    if (group != null) {
      group.getPreferences().put(key, value);
      return true;
    }
    return false;
  }

  /**
   * Remove a preference
   *
   * @param groupId the id of the group to remove the preference to
   * @param key the key of the preference
   * @return whether the preference was removed
   */
  @Receptor("group-remove-preference")
  public boolean setPreference(@ParamName("id") String groupId, @ParamName("key") String key) {
    BotGroup group = Guido.getDataLoader().getGroup(groupId);
    if (group != null) {
      group.getPreferences().remove(key);
      return true;
    }
    return false;
  }

  /**
   * Adds a permission to the given group
   *
   * @param id the id of the given group
   * @param context the context to add the permission on
   * @param permission the permission to add
   * @return true if the permission was added false otherwise
   */
  @Receptor("group-add-permission")
  public boolean addPermission(
      @ParamName("id") String id,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      return group.addPermission(context, permission.getNode(), permission.isEnabled());
    }
    return false;
  }

  /**
   * Add a parent of a group
   *
   * @param id the id of the group to add
   * @param parentId the id of the parent to add to the group
   * @return true if the parent was added
   */
  @Receptor("group-add-parent")
  public boolean addParent(@ParamName("id") String id, @ParamName("parent") String parentId) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group == null) return false;
    return group.getParents().add(parentId);
  }

  /**
   * Remove a parent of a group
   *
   * @param id the id of the group to remove
   * @param parentId the id of the parent to remove from the group
   * @return true if the parent was removed
   */
  @Receptor("group-remove-parent")
  public boolean removeParent(@ParamName("id") String id, @ParamName("parent") String parentId) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group == null) return false;
    return group.getParents().remove(parentId);
  }

  /**
   * Removes a permission to the given group
   *
   * @param id the id if the group
   * @param context the context to remove the permission from
   * @param permission the permission to remove
   * @return true if the permission was removed false otherwise
   */
  @Receptor("group-remove-permission")
  public boolean removePermission(
      @ParamName("id") String id,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      return group.removePermission(context, permission.getNode());
    }
    return false;
  }
}
