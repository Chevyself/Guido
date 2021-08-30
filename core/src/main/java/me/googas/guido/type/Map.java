package me.googas.guido.type;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.starbox.Strings;

/** Represents a map or arena that can be played in a game */
public class Map {

  @Getter @NonNull private final String name;
  @Getter @NonNull private final String description;
  @Getter @NonNull private final List<MapAuthor> authors;
  @Getter @NonNull private final List<String> tags;

  public Map(
      @NonNull String name,
      @NonNull String description,
      @NonNull List<MapAuthor> authors,
      @NonNull List<String> tags) {
    this.name = name;
    this.description = description;
    this.authors = authors;
    this.tags = tags;
  }

  /** @deprecated for gson use only */
  public Map() {
    this("Unknown", "No description provided", new ArrayList<>(), new ArrayList<>());
  }

  public boolean hasTag(@NonNull String tag, float similarity) {
    for (String mapTag : this.tags) {
      if (Strings.similarity(tag, mapTag) > similarity) {
        return true;
      }
    }
    return false;
  }
}
