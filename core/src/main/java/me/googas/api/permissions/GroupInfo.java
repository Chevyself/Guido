package me.googas.api.permissions;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.API;

/** Represents the information to getId a {@link Group} */
public class GroupInfo {

  @NonNull @Getter private final String id;
  @Getter private final int weight;
  @Getter private final String name;

  /**
   * Create the group information
   *
   * @param id the id of the group
   * @param weight the weight of the group
   * @param name the name of the group
   */
  public GroupInfo(@NonNull String id, int weight, String name) {
    this.id = id;
    this.weight = weight;
    this.name = name;
  }

  /** @deprecated this constructor may only be used by gson */
  public GroupInfo() {
    this("", 0, "");
  }

  /**
   * Get the real group using the info of this object
   *
   * @return the group if it is found null otherwise
   */
  public Group getGroup() {
    return API.getLoader().getGroups().getGroup(this.id);
  }
}
