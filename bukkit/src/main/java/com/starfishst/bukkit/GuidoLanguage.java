package com.starfishst.bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.starbox.Language;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class GuidoLanguage implements Language {

  @NonNull @Getter private final String locale;
  @NonNull @Getter private final ConfigurationSection section;

  public GuidoLanguage(@NonNull String locale, @NonNull ConfigurationSection section) {
    this.locale = locale;
    this.section = section;
  }

  @NonNull
  public static GuidoLanguage load(@NonNull ConfigurationSection section) {
    return new GuidoLanguage(section.getString("locale", "none"), section);
  }

  @NonNull
  public static List<GuidoLanguage> loadAll(@NonNull Plugin plugin, @NonNull String... locales) {
    ArrayList<GuidoLanguage> languages = new ArrayList<>();
    for (String locale : locales) {
      try {
        languages.add(GuidoLanguage.load(plugin, locale));
      } catch (IOException | InvalidConfigurationException e) {
        e.printStackTrace();
      }
    }
    return languages;
  }

  @NonNull
  public static GuidoLanguage load(@NonNull Plugin plugin, @NonNull String locale)
      throws IOException, InvalidConfigurationException {
    InputStream resource = plugin.getResource("lang/" + locale + ".yml");
    if (resource == null)
      throw new IOException(
          "Could not load locale "
              + locale
              + " as plugin "
              + plugin.getName()
              + " does not have the resource `lang/"
              + locale
              + ".yml");
    InputStreamReader reader = new InputStreamReader(resource);
    YamlConfiguration yaml = new YamlConfiguration();
    yaml.load(reader);
    return GuidoLanguage.load(yaml);
  }

  @Override
  public @NonNull Optional<?> getRaw(@NonNull String s) {
    return Optional.ofNullable(this.section.get(s));
  }

  @Override
  public @NonNull String get(@NonNull String s) {
    return this.section.getString(s, s);
  }

  @Override
  public @NonNull Object get(@NonNull String s, @NonNull Map<String, String> map) {
    String str = this.get(s);
    for (Map.Entry<String, String> entry : map.entrySet()) {
      str = str.replace("%" + entry.getKey() + "%", entry.getValue());
    }
    return str;
  }

  @Override
  public @NonNull Object get(@NonNull String s, Object... objects) {
    String str = this.get(s);
    for (int i = 0; i < objects.length; i++) {
      str = str.replace("%" + i + "%", String.valueOf(objects[i]));
    }
    return str;
  }

  @Override
  public String toString() {
    return "GuidoLanguage{" + "locale='" + locale + '\'' + ", section=" + section + '}';
  }
}
