package com.starfishst.bukkit.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.commons.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class ModuleSettings {

  @NonNull @Getter private final String name;
  @NonNull @Getter private final Map<String, Object> map;
  @Nullable @Getter private final ConfigurationSection section;

  public ModuleSettings(
      @NonNull String name,
      @NonNull Map<String, Object> map,
      @Nullable ConfigurationSection section) {
    this.name = name;
    this.map = map;
    this.section = section;
  }

  public ModuleSettings(@NonNull String name) {
    this(name, new HashMap<>(), null);
  }

  @NonNull
  public static List<ModuleSettings> loadAll(@Nullable ConfigurationSection modules) {
    if (modules == null) return new ArrayList<>();
    List<ModuleSettings> settings = new ArrayList<>();
    for (String key : modules.getKeys(false)) {
      settings.add(ModuleSettings.load(key, modules.getConfigurationSection(key)));
    }
    return settings;
  }

  @NonNull
  private static ModuleSettings load(@NonNull String name, @Nullable ConfigurationSection section) {
    Map<String, Object> map = new HashMap<>();
    if (section != null) {
      for (String key : section.getKeys(false)) {
        map.put(key, section.get(key));
      }
    }
    return new ModuleSettings(name, map, section);
  }

  public boolean getBoolean(@NonNull String key, boolean def) {
    return this.getOr(key, def, Boolean.class);
  }

  @NonNull
  public Number getNumber(@NonNull String key, @NonNull Number def) {
    return this.getOr(key, def, Number.class);
  }

  public double getDouble(@NonNull String key, double def) {
    return this.getNumber(key, def).doubleValue();
  }

  @NonNull
  private <T> T getOr(@NonNull String key, @NonNull T def, @NonNull Class<T> typeOfT) {
    return Validate.notNullOr(this.get(key, typeOfT), def);
  }

  @Nullable
  private <T> T get(@NonNull String key, @NonNull Class<T> typeOfT) {
    Object obj = this.map.get(key);
    if (obj == null) return null;
    if (typeOfT.isAssignableFrom(obj.getClass())) return typeOfT.cast(obj);
    return null;
  }

  public int getInt(@NonNull String key, int def) {
    return this.getNumber(key, def).intValue();
  }
}
