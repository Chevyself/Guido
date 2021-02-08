package com.starfishst.bukkit.lang;

import com.starfishst.commands.bukkit.utils.BukkitUtils;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.commons.maps.MapBuilder;
import org.bukkit.configuration.ConfigurationSection;

/** An implementation of locale file for bukkit */
public class BukkitLocaleFile implements LocaleFile {

  /** The section to getId the messages from */
  @NonNull private final ConfigurationSection section;

  /**
   * Create the locale file
   *
   * @param section the section to getId the messages from
   */
  public BukkitLocaleFile(@NonNull ConfigurationSection section) {
    this.section = section;
  }

  @Override
  public @NonNull String getLang() {
    return this.get("locale");
  }

  @Override
  public void save() {
    // NOTHING
  }

  @Override
  public String getRaw(@NonNull String s) {
    return this.section.getString(s);
  }

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @Override
  public @NonNull String get(@NonNull String path) {
    return BukkitUtils.build(LocaleFile.super.get(path));
  }

  /**
   * Get the string and build it with placeholders. It will replace the placeholders that are inside
   * a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @Override
  public @NonNull String get(@NonNull String path, @NonNull Map<String, String> placeholders) {
    return BukkitUtils.build(LocaleFile.super.get(path, placeholders));
  }

  /**
   * Get the string and build it with placeholders using a builder. It will replace the placeholders
   * that are inside a "%" character
   *
   * @param path the path that leads to the string
   * @param placeholders the string to build the string
   * @return the built string
   */
  @Override
  public @NonNull String get(
      @NonNull String path, @NonNull MapBuilder<String, String> placeholders) {
    return BukkitUtils.build(LocaleFile.super.get(path, placeholders));
  }
}
