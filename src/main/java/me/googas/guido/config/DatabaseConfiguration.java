package me.googas.guido.config;

import lombok.Getter;
import lombok.NonNull;

public class DatabaseConfiguration {

  @NonNull @Getter private final String url;

  public DatabaseConfiguration(@NonNull String url) {
    this.url = url;
  }

  public DatabaseConfiguration() {
    this("");
  }
}
