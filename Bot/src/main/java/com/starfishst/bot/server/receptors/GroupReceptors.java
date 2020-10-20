package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGroup;
import com.starfishst.bot.handlers.data.GuidoGroup;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.guido.api.data.Group;
import java.util.Collection;
import java.util.HashSet;
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
  @Receptor(method = "group")
  public BotGroup group(@ParamName(name = "id") String id) {
    return Guido.getDataLoader().getGroup(id);
  }

  /**
   * The receptor to get all the groups
   *
   * @return the existing groups
   */
  @Receptor(method = "groups")
  public Collection<Group<?, ?>> groups() {
    return Guido.getDataLoader().getGroups();
  }

  /**
   * Create a group and get an id for it
   *
   * @return the id of the group created
   */
  @Receptor(method = "create-group")
  public String createGroup() {
    return new GuidoGroup(new GuidoValuesMap(), new HashSet<>()).getId();
  }

  /**
   * Set a preference
   *
   * @param groupId the id of the group to set the preference to
   * @param key the key of the preference
   * @param value the value of the preference
   * @return whether the preference was set
   */
  @Receptor(method = "group-set-preference")
  private boolean setPreference(
      @ParamName(name = "id") String groupId,
      @ParamName(name = "key") String key,
      @ParamName(name = "value") Object value) {
    BotGroup group = Guido.getDataLoader().getGroup(groupId);
    if (group != null) {
      group.getPreferences().addValue(key, value);
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
  @Receptor(method = "group-add-permission")
  public boolean addPermission(
      @ParamName(name = "id") String id,
      @ParamName(name = "context") String context,
      @ParamName(name = "permission") GuidoPermission permission) {
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
  @Receptor(method = "group-remove-permission")
  public boolean removePermission(
      @ParamName(name = "id") String id,
      @ParamName(name = "context") String context,
      @ParamName(name = "permission") GuidoPermission permission) {
    BotGroup group = Guido.getDataLoader().getGroup(id);
    if (group != null) {
      return group.refresh().removePermission(context, permission.getNode());
    }
    return false;
  }
}
