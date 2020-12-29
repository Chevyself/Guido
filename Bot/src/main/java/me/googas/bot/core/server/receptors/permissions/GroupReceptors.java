package me.googas.bot.core.server.receptors.permissions;

import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.permissions.GuidoGroup;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class GroupReceptors {

  @Receptor("group/create")
  public Group create(
      @ParamName("weight") int weight,
      @ParamName("preferences") GuidoValuesMap preferences,
      @ParamName("permissions") Set<PermissionStack> permissions,
      @ParamName("name") String name,
      @ParamName("parents") List<String> parents) {
    return new GuidoGroup(weight, preferences, permissions, name, parents).cache();
  }

  @Receptor("group/weight")
  public boolean setWeight(@ParamName("id") String id, @ParamName("weight") int weight) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.setWeight(weight);
    return true;
  }

  @Receptor("group/name")
  public boolean setName(@ParamName("id") String id, @ParamName("name") String name) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.setName(name);
    return true;
  }

  @Receptor("group/preference")
  public boolean setPreference(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.getPreferences().put(key, value);
    return true;
  }

  @Receptor("group/remove-preference")
  public boolean removePreference(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.getPreferences().put(key, value);
    return true;
  }

  @Receptor("group/parent")
  public boolean addParent(@ParamName("id") String id, @ParamName("parent") String parent) {
    Group group = this.getGroup(id);
    Group parentGroup = this.getGroup(parent);
    if (group == null || parentGroup == null) return false;
    return group.getParents().add(parentGroup.getId());
  }

  @Receptor("group/remove-parent")
  public boolean removeParent(@ParamName("id") String id, @ParamName("parent") String parent) {
    Group group = this.getGroup(id);
    Group parentGroup = this.getGroup(parent);
    if (group == null || parentGroup == null) return false;
    return group.getParents().remove(parentGroup.getId());
  }

  @Nullable
  public Group getGroup(@NonNull String id) {
    return Guido.getDataLoader().getGroup(id);
  }
}
