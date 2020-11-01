package me.googas.bot.core.server.receptors;

import java.util.Collection;
import java.util.HashSet;
import me.googas.api.permissions.Group;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoGroup;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.bot.core.types.permissions.GuidoPermission;
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
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      return group.refresh();
    }
    return null;
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
   * @return the id of the group created
   */
  @Receptor("create-group")
  public String createGroup(@ParamName("name") String name) {
    return new GuidoGroup(new GuidoValuesMap(), new HashSet<>(), name).getId();
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
  private boolean setPreference(
      @ParamName("id") String groupId,
      @ParamName("key") String key,
      @ParamName("value") Object value) {
    BotGroup group = Guido.getDataLoader().getGroup(groupId);
    if (group != null) {
      group.refresh().getPreferences().addValue(key, value);
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
      @ParamName("permission") GuidoPermission permission) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      return group.refresh().addPermission(context, permission.getNode(), permission.isEnabled());
    }
    return false;
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
      @ParamName("permission") GuidoPermission permission) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      return group.refresh().removePermission(context, permission.getNode());
    }
    return false;
  }
}
