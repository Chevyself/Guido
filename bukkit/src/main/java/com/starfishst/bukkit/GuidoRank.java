package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.modules.GroupsHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.permissions.AbstractPermission;
import me.googas.api.permissions.Group;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.starbox.modules.data.type.Rank;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class GuidoRank implements Rank {

  @NonNull private final Group group;

  public GuidoRank(@NonNull Group group) {
    this.group = group;
  }

  private boolean isDefault() {
    return this.getBoolean("global", "default", false);
  }

  @Override
  public @NonNull ItemStack toItem() {
    Material material = Material.STONE;
    try {
      material = Material.getMaterial(this.getString("global", "material", "stone"));
    } catch (IllegalArgumentException ignored) {
    }
    return new ItemStack(material);
  }

  @Override
  public @NonNull Map<String, Map<String, Boolean>> getPermissions() {
    return this.group.toMap();
  }

  @Nullable
  @Override
  public String getPrefix(@Nullable String context) {
    return this.group.getString(context, "prefix", "");
  }

  @Nullable
  @Override
  public String getSuffix(@Nullable String context) {
    return this.group.getString(context, "suffix", "");
  }

  @Override
  public void setPrefix(@Nullable String context, @Nullable String prefix) {
    this.group.setString(context, "prefix", prefix);
  }

  @Override
  public void setSuffix(@Nullable String context, @Nullable String prefix) {
    this.group.setString(context, "suffix", prefix);
  }

  @Override
  public @NonNull Map<String, Map<String, Object>> getInformation() {
    return this.group.getInformation();
  }

  @Override
  public @NonNull List<Permission> toBukkit() {
    List<Permission> permissions = new ArrayList<>();
    for (Map.Entry<String, Map<String, Boolean>> entry : this.getPermissions().entrySet()) {
      permissions.add(
          new Permission(
              this.getNode(entry.getKey()),
              this.isDefault() ? PermissionDefault.TRUE : PermissionDefault.FALSE,
              entry.getValue()));
    }
    return permissions;
  }

  @Override
  public @NonNull String getNode(@Nullable String context) {
    String node = this.getNode();
    if (context != null) {
      node = node + "." + context.toLowerCase().replace(" ", "_");
    }
    return node;
  }

  @Override
  public @NonNull String getNode() {
    return "guido.group." + this.getName().toLowerCase().replace(" ", "_");
  }

  @Override
  public int getWeight() {
    return this.group.getWeight();
  }

  @Override
  public @NonNull String getName() {
    return this.group.getName();
  }

  @Nullable
  @Override
  public String getDescription() {
    return this.group.getString("global", "description", "No description");
  }

  @Nullable
  @Override
  public String getDisplayName() {
    return this.group.getString("global", "display-name", this.getName());
  }

  @Override
  public @NonNull List<Rank> getParents() {
    return Guido.getModuleRegistry().require(GroupsHandler.class).getParentsAsRanks(this.group);
  }

  @Override
  public void set(@Nullable String context, @NonNull String node, @NonNull Object value) {
    try {
      Boolean aBoolean =
          Requests.Groups.setPreference(
                  this.group.getId(), context == null ? "global" : context, node, value)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        Rank.super.set(context, node, value);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean addPermission(@Nullable String context, @NonNull String permission) {
    try {
      Boolean aBoolean =
          Requests.Groups.addPermission(
                  this.group.getId(),
                  context == null ? "global" : context,
                  new AbstractPermission(permission, true, -1))
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Rank.super.addPermission(context, permission);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean removePermission(@Nullable String context, @NonNull String permission) {
    try {
      Boolean aBoolean =
          Requests.Groups.removePermission(
                  this.group.getId(), context == null ? "global" : context, permission)
              .send(Guido.getClient().getConnection());
      if (aBoolean != null && aBoolean) {
        return Rank.super.removePermission(context, permission);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
    return false;
  }
}
