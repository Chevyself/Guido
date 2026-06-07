package me.googas.server;

import lombok.NonNull;
import me.googas.api.Range;

public interface RankRange extends Range {
  @NonNull
  String getLadder();

  @NonNull
  String getName();

  long getRoleId();
}
