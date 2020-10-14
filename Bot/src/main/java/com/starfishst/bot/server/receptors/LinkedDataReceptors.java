package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotLinkedData;
import com.starfishst.bot.handlers.data.GuidoPermissionStack;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors associated with linked data requests */
public class LinkedDataReceptors {

  /**
   * Check if data exists for the next type and identification
   *
   * @param type the type to check
   * @param identification the identification of the type
   * @return true if the data exists else false
   */
  @Receptor(method = "data-exists")
  public boolean exists(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification) {
    return Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification)) != null;
  }

  /**
   * Set the linked user for the given data type
   *
   * @param type the type to set the user t o
   * @param identification the way to identify the data
   * @param user the user to set as linked
   * @return true if the user was set false if the data does not exist
   */
  @Receptor(method = "set-user")
  public boolean setUser(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification,
      @ParamName(name = "user") BotUser user) {
    BotLinkedData linkedData =
        Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification));
    if (linkedData != null) {
      linkedData.setLinkedUser(user);
      return true;
    }
    return false;
  }

  /**
   * Get the permission of certain linked data in a given context
   *
   * @param type the type of linked data to get
   * @param identification the identification
   * @param context the context to get the permissions on
   * @return the permission stack of the data
   */
  @Receptor(method = "permission")
  public PermissionStack<?> permissions(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification,
      @ParamName(name = "context") String context) {
    BotLinkedData linkedData =
        Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification));
    if (linkedData != null) {
      PermissionStack<?> permissions = linkedData.getPermissions(context);
      if (permissions != null) {
        return permissions;
      }
    }
    return new GuidoPermissionStack(context, new HashSet<>());
  }

  /**
   * Get the preferences that the linked data has
   *
   * @param type the type of linked data
   * @param identification the way to identify the data
   * @return the preferences
   */
  @Receptor(method = "preferences")
  public Map<String, Object> preferences(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification) {
    BotLinkedData data =
        Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification));
    if (data != null) {
      return data.getPreferences().getMap();
    }
    return new HashMap<>();
  }

  /**
   * Get the stats that the linked data has
   *
   * @param type the type of linked data
   * @param identification the way to identify the data
   * @return the stats
   */
  @Receptor(method = "stats")
  public Map<String, Double> stats(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification) {
    BotLinkedData data =
        Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification));
    if (data != null) {
      return data.getStats();
    }
    return new HashMap<>();
  }

  /**
   * Save the stats for the given data
   *
   * @param type the type of the given data
   * @param identification the way to identify the data
   * @param stats the stats to save
   * @return true if the stats were saved false if the data does not exist
   */
  @Receptor(method = "save-stats")
  public boolean saveStats(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification,
      @ParamName(name = "stats") Map<String, Double> stats) {
    BotLinkedData data =
        Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification));
    if (data != null) {
      stats.forEach(
          (key, value) -> data.getStats().put(key, data.getStats().getOrDefault(key, 0D) + value));
      return true;
    }
    return false;
  }
}
