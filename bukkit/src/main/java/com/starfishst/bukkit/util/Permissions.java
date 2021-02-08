package com.starfishst.bukkit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.permissions.AbstractPermission;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.Lots;

public class Permissions {

  @NonNull
  public static Map<String, Boolean> getChildren(
      @NonNull Group group, @Nullable Collection<Group> parents, @NonNull String context) {
    parents = parents == null ? new ArrayList<>() : parents;
    Map<String, Boolean> children = new HashMap<>();
    Set<PermissionStack> stacks =
        Lots.set(group.getPermissions(context), group.getPermissions("global"));
    for (PermissionStack stack : stacks) {
      if (stack == null) continue;
      for (AbstractPermission abstractPermission : stack.getPermissions()) {
        if (!abstractPermission.isExpired())
          children.put(abstractPermission.getNode(), abstractPermission.isEnabled());
      }
    }
    if (parents.isEmpty()) return children;
    for (Group parent : parents) {
      children.putAll(Permissions.getChildren(parent, null, context));
    }
    return children;
  }
}
