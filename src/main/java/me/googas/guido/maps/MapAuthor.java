package me.googas.guido.maps;

import lombok.Getter;
import lombok.NonNull;

public class MapAuthor {

  @Getter @NonNull private final String name;

  public MapAuthor(@NonNull String name) {
    this.name = name;
  }
}
