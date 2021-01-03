package com.starfishst.bukkit.dependencies.pgm.listeners.groups;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.listeners.GroupsListener;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.Lots;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import tc.oc.pgm.PGMConfig;
import tc.oc.pgm.api.Config;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.Permissions;

public class PGMGroupsListener implements GuidoListener {

  /** Adds the loaded groups in PGM */
  public void addInPGM(@NonNull Collection<Group> groups) {
    if (!Guido.isPgmConnected())
      throw new IllegalStateException(
          "Groups cannot be added to PGM when it is not in the plugins folder or enabled");
    Config configuration = PGM.get().getConfiguration();
    if (!(configuration instanceof PGMConfig)) return;
    PGMConfig config = (PGMConfig) configuration;
    for (Group group : groups) {
      PGMConfig.Group toPGM = this.toPGM(group);
      if (this.notLoaded(toPGM)) config.getGroups().add(toPGM);
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
        group.getName().toLowerCase().replace(" ", "-"),
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
   * Checks if a group is not loaded already
   *
   * @param toPGM the group to check if it is not loaded
   * @return true if the group is not loaded else false
   */
  private boolean notLoaded(@NonNull PGMConfig.Group toPGM) {
    for (Config.Group group : PGM.get().getConfiguration().getGroups()) {
      if (group.getId().equals(toPGM.getId())) return false;
    }
    return true;
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
    if (context == null)
      return Permissions.register(
          new Permission(
              this.groups().getNode(group),
              this.groups().isDefault(group) ? PermissionDefault.TRUE : PermissionDefault.FALSE,
              new HashMap<>()));
    Map<String, Boolean> permissions = new HashMap<>();
    Set<PermissionStack> stacks =
        Lots.set(
            group.getPermissions(context),
            group.getPermissions(Guido.getConfiguration().getContext()),
            group.getPermissions("global"));
    for (PermissionStack stack : stacks) {
      if (stack != null) {
        for (me.googas.api.permissions.Permission permission : stack.getPermissions()) {
          permissions.put(permission.getNode(), permission.isEnabled());
        }
      }
    }
    return Permissions.register(
        new Permission(
            this.groups().getNode(group) + "." + context, PermissionDefault.OP, permissions));
  }

  public void unloadGroups(@NonNull Collection<Group> groups) {
    for (Group group : groups) {
      Bukkit.getServer().getPluginManager().removePermission(this.groups().getNode(group));
      Bukkit.getServer()
          .getPluginManager()
          .removePermission(this.groups().getNode(group) + ".observer-permissions");
      Bukkit.getServer()
          .getPluginManager()
          .removePermission(this.groups().getNode(group) + ".participant-permissions");
    }
  }

  @NonNull
  private GroupsListener groups() {
    return Guido.getListener(GroupsListener.class);
  }

  @Override
  public @NonNull String getName() {
    return "pgm-groups";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
