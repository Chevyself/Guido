package me.googas.api.server.receptors;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.Requests;
import me.googas.api.ValuesMap;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.permissions.Permission;
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

  @Receptor(Requests.Groups.GROUP)
  public Group getGroup(@ParamName("id") String id) {
    return this.loader.getGroup(id);
  }

  @Receptor(Requests.Groups.GROUPS_SIZE)
  public long size(@ParamName("size") int size) {
    return this.loader.maxPageGroups(size);
  }

  @Receptor(Requests.Groups.GROUPS)
  public Collection<GroupInfo> getGroups(@ParamName("page") int page, @ParamName("size") int size) {
    return this.loader.getGroups(page, size);
  }

  @Receptor(Requests.Groups.DELETE)
  public boolean delete(@NonNull String id) {
    return this.loader.deleteGroup(id);
  }

  @Receptor(Requests.Groups.CREATE)
  public Group create(
      @ParamName("name") String name,
      @ParamName("weight") int weight,
      @ParamName("preferences") ValuesMap preferences,
      @ParamName("permissions") Set<PermissionStack> permissions,
      @ParamName("parents") List<String> parents) {
    return this.groupSupplier.create(weight, preferences, permissions, name, parents);
  }

  @Receptor(Requests.Groups.WEIGHT)
  public boolean setWeight(@ParamName("id") String id, @ParamName("weight") int weight) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.setWeight(weight);
    return true;
  }

  @Receptor(Requests.Groups.NAME)
  public boolean setName(@ParamName("id") String id, @ParamName("name") String name) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.setName(name);
    return true;
  }

  @Receptor(Requests.Groups.PREFERENCE)
  public boolean setPreference(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.getPreferences().put(key, value);
    return true;
  }

  @Receptor(Requests.Groups.REMOVE_PREFERENCE)
  public boolean removePreference(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    group.getPreferences().put(key, value);
    return true;
  }

  @Receptor(Requests.Groups.ADD_PERMISSION)
  public boolean addPermission(
      @ParamName("id") String id,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    return group.addPermission(
        context, permission.getNode(), permission.isEnabled(), permission.expires());
  }

  @Receptor(Requests.Groups.REMOVE_PERMISSION)
  public boolean addPermission(
      @ParamName("id") String id,
      @ParamName("context") String context,
      @ParamName("node") String node) {
    Group group = this.getGroup(id);
    if (group == null) return false;
    return group.removePermission(context, node);
  }

  @Receptor(Requests.Groups.PARENT)
  public boolean addParent(@ParamName("id") String id, @ParamName("parent") String parent) {
    Group group = this.getGroup(id);
    Group parentGroup = this.getGroup(parent);
    if (group == null || parentGroup == null) return false;
    return group.getParents().add(parentGroup.getId());
  }

  @Receptor(Requests.Groups.REMOVE_PARENT)
  public boolean removeParent(@ParamName("id") String id, @ParamName("parent") String parent) {
    Group group = this.getGroup(id);
    Group parentGroup = this.getGroup(parent);
    if (group == null || parentGroup == null) return false;
    return group.getParents().remove(parentGroup.getId());
  }

  @Receptor(Requests.Groups.ALL_GROUPS)
  public Collection<Group> getAllGroups() {
    return this.loader.getGroups();
  }

  public interface GroupSupplier {
    @NonNull
    Group create(
        int weight,
        @NonNull ValuesMap preferences,
        @NonNull Set<PermissionStack> permissions,
        @NonNull String name,
        @NonNull List<String> parents);
  }
}
