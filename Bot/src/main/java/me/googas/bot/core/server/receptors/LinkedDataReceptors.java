package me.googas.bot.core.server.receptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableInfo;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.permissions.GuidoPermissionStack;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors associated with linked data requests */
public class LinkedDataReceptors {

  /**
   * Check if data exists for the next type and identification
   *
   * @param info the information of the link to check if it exists
   * @return true if the data exists else false
   */
  @Receptor("data-exists")
  public boolean exists(@ParamName("info") LinkableInfo info) {
    return info.getLink() != null;
  }

  /**
   * Set the linked user for the given data type
   *
   * @param info the information of the link to set the user to
   * @param userId the id of the user to set as linked
   * @return true if the user was set false if the data does not exist
   */
  @Receptor("set-user")
  public boolean setUser(@ParamName("info") LinkableInfo info, @ParamName("user") String userId) {
    LinkableData data = info.getLink();
    if (data != null) {
      UserData user = Guido.getDataLoader().getUserData(userId);
      data.setLinkedUser(user);
      return true;
    }
    return false;
  }

  /**
   * Get the permission of certain linked data in a given context
   *
   * @param info the information of the data to get the permissions from
   * @param context the context to get the permissions on
   * @return the permission stack of the data
   */
  @Receptor("permission")
  public PermissionStack permissions(
      @ParamName("info") LinkableInfo info, @ParamName("context") String context) {
    LinkableData linkableData = info.getLink();
    if (linkableData != null) {
      GuidoPermissionStack permissions = new GuidoPermissionStack(context, new HashSet<>());
      PermissionStack stack = linkableData.getPermissions(context);
      PermissionStack global = linkableData.getPermissions("global");
      if (stack != null) {
        permissions.getPermissions().addAll(stack.getPermissions());
      }
      if (global != null) {
        permissions.getPermissions().addAll(global.getPermissions());
      }
      return permissions;
    }
    return new GuidoPermissionStack(context, new HashSet<>());
  }

  /**
   * Get the preferences that the linked data has
   *
   * @param info the information of the data to get the preferences from
   * @return the preferences
   */
  @Receptor("preferences")
  public Map<String, Object> preferences(@ParamName("info") LinkableInfo info) {
    LinkableData data = info.getLink();
    if (data != null) {
      return data.getPreferences().getMap();
    }
    return new HashMap<>();
  }

  /**
   * Get the stats that the linked data has
   *
   * @param info the information of the data to get the stats from
   * @return the stats
   */
  @Receptor("stats")
  public Map<String, Float> stats(@ParamName("info") LinkableInfo info) {
    LinkableData data = info.getLink();
    if (data != null) {
      return data.getStats();
    }
    return new HashMap<>();
  }

  /**
   * Reset the stats of the linked data
   *
   * @param info the information of the data to reset the stats
   * @return whether the stats were reset
   */
  @Receptor("reset-stats")
  public boolean resetStats(@ParamName("info") LinkableInfo info) {
    LinkableData data = info.getLink();
    if (data != null) {
      data.getStats().clear();
      return true;
    }
    return false;
  }

  /**
   * Check whether linked data is linked
   *
   * @param info the information of the data to check whether it is linked
   * @return true if the data is linked
   */
  @Receptor("is-linked")
  public boolean isLinked(@ParamName("info") LinkableInfo info) {
    LinkableData data = info.getLink();
    if (data != null) {
      return data.isLinked();
    }
    return false;
  }

  /**
   * Save the stats for the given data
   *
   * @param info the information of the data to save the stats
   * @param stats the stats to save
   * @return true if the stats were saved false if the data does not exist
   */
  @Receptor("save-stats")
  public boolean saveStats(
      @ParamName("info") LinkableInfo info, @ParamName("stats") Map<String, Double> stats) {
    LinkableData data = info.getLink();
    if (data != null) {
      stats.forEach((key, value) -> data.increaseStat(key, (float) value.doubleValue()));
      return true;
    }
    return false;
  }

  /**
   * Adds a permission to the given linked type
   *
   * @param info the information of the data to add the permission to
   * @param context the context to add the permission on
   * @param permission the permission to add
   * @return true if the permission was added false otherwise
   */
  @Receptor("add-permission")
  public boolean addPermission(
      @ParamName("info") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    LinkableData data = info.getLink();
    if (data != null) {
      return data.addPermission(context, permission.getNode(), permission.isEnabled());
    }
    return false;
  }

  /**
   * Removes a permission to the given linked type
   *
   * @param info the information of the data to remove the permission to
   * @param context the context to remove the permission from
   * @param permission the permission to remove
   * @return true if the permission was removed false otherwise
   */
  @Receptor("remove-permission")
  public boolean removePermission(
      @ParamName("info") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    LinkableData data = info.getLink();
    if (data != null) {
      return data.removePermission(context, permission.getNode());
    }
    return false;
  }
}
