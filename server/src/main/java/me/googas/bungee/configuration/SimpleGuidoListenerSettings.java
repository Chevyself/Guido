package me.googas.bungee.configuration;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

public class SimpleGuidoListenerSettings implements GuidoListenerSettings {

  @NonNull @Getter private final String name;

  @NonNull @Getter private final Map<String, Object> settings;

  public SimpleGuidoListenerSettings(@NonNull String name, @NonNull Map<String, Object> settings) {
    this.name = name;
    this.settings = settings;
  }
}
