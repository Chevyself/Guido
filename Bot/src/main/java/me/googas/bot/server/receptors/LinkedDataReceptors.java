package me.googas.bot.server.receptors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.googas.api.Permission;
import me.googas.api.PermissionStack;
import me.googas.api.UserData;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedInfo;
import me.googas.bot.Guido;
import me.googas.bot.handlers.data.types.permissions.GuidoPermission;
import me.googas.bot.handlers.data.types.permissions.GuidoPermissionStack;
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
  public boolean exists(@ParamName("info") LinkedInfo info) {
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
  public boolean setUser(@ParamName("info") LinkedInfo info, @ParamName("user") String userId) {
    LinkedData data = info.getLink();
    if (data != null) {
      UserData user = Guido.getDataLoader().getUserData(userId);
      data.refresh().setLinkedUser(user);
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
      @ParamName("info") LinkedInfo info, @ParamName("context") String context) {
    LinkedData linkedData = info.getLink();
    if (linkedData != null) {
      PermissionStack permissions = linkedData.refresh().getPermissions(context);
      if (permissions != null) {
        return permissions;
      }
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
  public Map<String, Object> preferences(@ParamName("info") LinkedInfo info) {
    LinkedData data = info.getLink();
    if (data != null) {
      return data.refresh().getPreferences().getMap();
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
  public Map<String, Float> stats(@ParamName("info") LinkedInfo info) {
    LinkedData data = info.getLink();
    if (data != null) {
      return data.refresh().getStats();
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
  public boolean resetStats(@ParamName("info") LinkedInfo info) {
    LinkedData data = info.getLink();
    if (data != null) {
      data.refresh().getStats().clear();
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
  public boolean isLinked(@ParamName("info") LinkedInfo info) {
    LinkedData data = info.getLink();
    if (data != null) {
      return data.refresh().isLinked();
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
      @ParamName("info") LinkedInfo info, @ParamName("stats") Map<String, Number> stats) {
    LinkedData data = info.getLink();
    if (data != null) {
      stats.forEach((key, value) -> data.refresh().increaseStat(key, value.floatValue()));
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
      @ParamName("info") LinkedInfo info,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    LinkedData data = info.getLink();
    if (data != null) {
      return data.refresh().addPermission(context, permission.getNode(), permission.isEnabled());
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
      @ParamName("info") LinkedInfo info,
      @ParamName("context") String context,
      @ParamName("permission") GuidoPermission permission) {
    LinkedData data = info.getLink();
    if (data != null) {
      return data.refresh().removePermission(context, permission.getNode());
    }
    return false;
  }
}
