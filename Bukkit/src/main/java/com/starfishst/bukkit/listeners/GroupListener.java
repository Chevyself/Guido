package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.Lots;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import tc.oc.pgm.PGMConfig;
import tc.oc.pgm.api.Config;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.Permissions;

/** Listens for groups */
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
            Guido.getLogger().info("Groups loaded " + newGroups);
            if (consumer != null) {
              consumer.accept(newGroups);
            }
            Guido.getLogger().info(Guido.isPgmConnected() + " pgm is connected");
            if (Guido.isPgmConnected()) {
              this.addInPGM();
            }
          });
    }
  }

  /** Adds the loaded groups in PGM */
  public void addInPGM() {
    Logger log = Guido.getLogger();
    if (!Guido.isPgmConnected())
      throw new IllegalStateException(
          "Groups cannot be added to PGM when it is not in the plugins folder or enabled");
    Config configuration = PGM.get().getConfiguration();
    log.info("PGM Config: " + configuration);
    if (!(configuration instanceof PGMConfig)) return;
    PGMConfig config = (PGMConfig) configuration;
    for (Group group : this.groups) {
      PGMConfig.Group toPGM = this.toPGM(group);
      log.info(toPGM.getPermission().getName() + " has been loaded");
      config.getGroups().add(toPGM);
    }
  }

  /**
   * Convert a group into a PGM group
   *
   * @param group the group to convert
   * @return the converted group
   */
  @NonNull
  public PGMConfig.Group toPGM(@NonNull Group group) {
    ValuesMap preferences = group.getPreferences();
    return new PGMConfig.Group(
        group.getName().replace(" ", "-"),
        new PGMConfig.Flair(
            this.getComponent(group, "pgm-prefix"),
            this.getComponent(group, "pgm-suffix"),
            this.getComponent(group, "display-name"),
            this.getComponent(group, "description"),
            preferences.get("link", String.class),
            null,
            null),
        this.toBukkit(group, null),
        this.toBukkit(group, "observer-permissions"),
        this.toBukkit(group, "participant-permissions"));
  }

  /**
   * Get the component from a group and a key
   *
   * @param group the group to get the preferences
   * @param key the key of the preference to get the component
   * @return the component as a string
   */
  public String getComponent(@NonNull Group group, @NonNull String key) {
    String string = group.getPreferences().get(key, String.class);
    if (string == null) return null;
    return BukkitUtils.build(string);
  }

  /**
   * Convert the permission stack into a bukkit permission
   *
   * @param group the group to get the stack from
   * @param context the context to get the stack
   * @return the permission stack as a bukkit permission
   */
  @NonNull
  public Permission toBukkit(@NonNull Group group, String context) {
    String contextToUse = context == null ? Guido.getConfiguration().getContext() : context;
    PermissionStack stack = group.getPermissions(contextToUse);
    Map<String, Boolean> permissions = new HashMap<>();
    if (stack != null) {
      for (me.googas.api.permissions.Permission permission : stack.getPermissions()) {
        permissions.put(permission.getNode(), permission.isEnabled());
      }
    }
    return Permissions.register(
        new Permission(this.getNode(group, context), PermissionDefault.FALSE, permissions));
  }

  /**
   * Get the node of a context
   *
   * @param group the group that will be appended in the node
   * @param context the context of the permission
   * @return the node of the permission
   */
  @NonNull
  public String getNode(@NonNull Group group, String context) {
    if (context == null) {
      return "guido.group." + group.getName().replace(" ", "-");
    }
    return "guido.group." + group.getName().replace(" ", "-") + "." + context;
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
   * Get the group as a permission node
   *
   * @param group the group to get as a permission node
   * @return the group
   */
  public String toPermission(@NonNull Group group) {
    return "guido.group." + group.getName().replace(" ", "-");
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "groups";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
