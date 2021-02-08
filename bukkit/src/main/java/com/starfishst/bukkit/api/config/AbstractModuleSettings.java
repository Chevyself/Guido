package com.starfishst.bukkit.api.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;

public class AbstractModuleSettings implements GuidoModuleSettings {

  @NonNull @Getter private final String name;
  @NonNull @Getter private final Map<String, Object> map;
  @Nullable @Getter private final ConfigurationSection section;

  public AbstractModuleSettings(
      @NonNull String name,
      @NonNull Map<String, Object> map,
      @Nullable ConfigurationSection section) {
    this.name = name;
    this.map = map;
    this.section = section;
  }

  public AbstractModuleSettings(@NonNull String name) {
    this(name, new HashMap<>(), null);
  }
}
