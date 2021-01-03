package com.starfishst.bungee.core.configuration;

import com.starfishst.bungee.api.configuration.GuidoListenerSettings;
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
