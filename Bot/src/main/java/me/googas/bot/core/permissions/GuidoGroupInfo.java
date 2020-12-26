package me.googas.bot.core.permissions;

import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.bot.Guido;

public class GuidoGroupInfo implements GroupInfo {

  @NonNull private final String id;
  private final int weight;
  private final String name;

  public GuidoGroupInfo(@NonNull String id, int weight, String name) {
    this.id = id;
    this.weight = weight;
    this.name = name;
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public int getWeight() {
    return this.weight;
  }

  @Override
  public @NonNull String getName() {
    return this.name;
  }

  @Override
  public Group getGroup() {
    return Guido.getDataLoader().getGroup(this.id);
  }
}
