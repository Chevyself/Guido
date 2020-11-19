package com.starfishst.bukkit.lang;

import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.commons.maps.MapBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation of locale file for bukkit */
public class BukkitLocaleFile implements LocaleFile {

  /** The section to get the messages from */
  @NotNull private final ConfigurationSection section;

  /**
   * Create the locale file
   *
   * @param section the section to get the messages from
   */
  public BukkitLocaleFile(@NotNull ConfigurationSection section) {
    this.section = section;
  }

  @Override
  public @NotNull String getLang() {
    return this.get("locale");
  }

  @Override
  public void save() {
    // NOTHING
  }

  @Override
  public @Nullable String getRaw(@NotNull String s) {
    return this.section.getString(s);
  }

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @Override
  public @NotNull String get(@NotNull String path) {
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
  public @NotNull String get(@NotNull String path, @NotNull Map<String, String> placeholders) {
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
  public @NotNull String get(
      @NotNull String path, @NotNull MapBuilder<String, String> placeholders) {
    return BukkitUtils.build(LocaleFile.super.get(path, placeholders));
  }
}
