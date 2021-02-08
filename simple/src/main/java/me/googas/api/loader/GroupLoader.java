package me.googas.api.loader;

import java.util.Collection;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.commons.RandomUtils;

public interface GroupLoader extends DataLoader {

  /**
   * Get a group matching the given id
   *
   * @param id the id of the group
   * @return the group if found else null
   */
  @Nullable
  Group getGroup(@NonNull String id);

  /**
   * Get a greoup matching the given name
   *
   * @param name the name of the grou[
   * @return the group if found else null
   */
  @Nullable
  Group getGroupByBane(@NonNull String name);

  /**
   * Get how many groups there are
   *
   * @return the amount of groups that there is
   */
  long maxPageGroups(int size);

  /**
   * Get all the created groups but only the information of them
   *
   * @param page the page of groups to see
   * @param size the size of the groups per page
   * @return the created groups
   */
  @NonNull
  Collection<GroupInfo> getGroups(int page, int size);

  /**
   * Get all the created groups
   *
   * @return the created groups
   */
  @NonNull
  Collection<Group> getGroups();

  /**
   * Delete the group with the given id
   *
   * @param id the id of the group to delete
   * @return true if the group was deleted
   */
  boolean deleteGroup(String id);

  /**
   * Get a new id for a group
   *
   * @return the id of the new group
   */
  @NonNull
  default String nextGroupId() {
    String id = RandomUtils.nextString(6);
    if (this.getGroup(id) != null) return this.nextGroupId();
    return id;
  }
}
