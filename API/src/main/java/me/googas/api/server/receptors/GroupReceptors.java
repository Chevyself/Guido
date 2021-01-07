package me.googas.api.server.receptors;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.ValuesMap;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.permissions.PermissionStack;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class GroupReceptors {

  @NonNull private final GroupLoader loader;
  @NonNull @Getter @Setter private GroupSupplier groupSupplier;

  public GroupReceptors(@NonNull GroupLoader loader, @NonNull GroupSupplier groupSupplier) {
    this.loader = loader;
    this.groupSupplier = groupSupplier;
  }

  @Receptor("group")
  public Group getGroup(@NonNull String id) {
    return this.loader.getGroup(id);
  }

  @Receptor("group/groups-size")
  public long size(@ParamName("size") int size) {
    return this.loader.maxPageGroups(size);
  }

  @Receptor("group/groups")
  public Collection<GroupInfo> getGroups(@ParamName("page") int page, @ParamName("size") int size) {
    return this.loader.getGroups(page, size);
  }

  @Receptor("group/delete")
  public boolean delete(@NonNull String id) {
    return this.loader.deleteGroup(id);
  }

  @Receptor("group/all-groups")
  public Collection<Group> getAllGroups() {
    return this.loader.getGroups();
  }

  @Receptor("group/create")
  public Group create(
      @ParamName("weight") int weight,
      @ParamName("preferences") ValuesMap preferences,
      @ParamName("permissions") Set<PermissionStack> permissions,
      @ParamName("name") String name,
      @ParamName("parents") List<String> parents) {
    return this.groupSupplier.create(weight, preferences, permissions, name, parents);
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

  interface GroupSupplier {
    @NonNull
    Group create(
        int weight,
        @NonNull ValuesMap preferences,
        @NonNull Set<PermissionStack> permissions,
        @NonNull String name,
        @NonNull List<String> parents);
  }
}
