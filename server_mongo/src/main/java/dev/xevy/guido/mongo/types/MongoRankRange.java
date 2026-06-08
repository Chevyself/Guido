package dev.xevy.guido.mongo.types;

import lombok.Getter;
import lombok.NonNull;
import me.googas.server.RankRange;

public class MongoRankRange implements RankRange {

  @NonNull @Getter private final Document document;

  public MongoRankRange(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public @NonNull String getLadder() {
    return document.ladder;
  }

  @Override
  public @NonNull String getName() {
    return document.name;
  }

  @Override
  public long getRoleId() {
    return document.roleId;
  }

  @Override
  public int getMin() {
    return document.min;
  }

  @Override
  public int getMax() {
    return document.max;
  }

  public static class Document {
    public String ladder;
    public String name;
    public int min;
    public int max;
    public long roleId;
  }
}
