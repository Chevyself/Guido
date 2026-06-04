package me.googas.api.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.GuidoCatchable;
import me.googas.api.Informative;
import me.googas.api.events.group.GroupUnloadedEvent;

/** This class represents a group which can be used to have multiple permissions in one */
public class Group implements Permissible, GuidoCatchable, Informative {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final List<String> parents;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;
  @NonNull @Getter private final Set<PermissionStack> permissions;
  @NonNull @Getter @Setter private String name;
  @Getter @Setter private int weight;

  /**
   * Create the group
   *
   * @param id the id of the group
   * @param parents the parents of the group
   * @param information the information about the group
   * @param permissions the permissions of the group
   * @param name the name of the group
   * @param weight the weight of the group
   */
  public Group(
      @NonNull String id,
      @NonNull List<String> parents,
      @NonNull Map<String, Map<String, Object>> information,
      @NonNull Set<PermissionStack> permissions,
      @NonNull String name,
      int weight) {
    this.id = id;
    this.parents = parents;
    this.information = information;
    this.permissions = permissions;
    this.name = name;
    this.weight = weight;
  }

  /** @deprecated this constructor may only be used by gson */
  public Group() {
    this("", new ArrayList<>(), new HashMap<>(), new HashSet<>(), "", 0);
  }

  @Override
  public void onRemove() {
    new GroupUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Group cache() {
    return (Group) GuidoCatchable.super.cache();
  }
}
