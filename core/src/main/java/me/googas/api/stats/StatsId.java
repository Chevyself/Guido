package me.googas.api.stats;

import lombok.NonNull;

public interface StatsId {
  @NonNull
  String getContext();

  @NonNull
  Object getLinkableId();

  @NonNull
  StatsLinkableType getLinkableType();
}
