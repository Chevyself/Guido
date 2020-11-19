package com.starfishst.bungee.core.lang;

import com.starfishst.bungee.utils.BungeeUtils;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.commons.maps.MapBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The implementation for bungee of locale file */
public class BungeeLocaleFile implements LocaleFile {

  /** The configuration to get the messages from */
  @NotNull private final Configuration config;

  /**
   * Create the bungee locale file
   *
   * @param config the configuration to get the messages from
   */
  public BungeeLocaleFile(@NotNull Configuration config) {
    this.config = config;
  }

  /**
   * Get the component from a given key
   *
   * @param key the key to get from the file and turn it into component
   * @return the component from the given key
   */
  public @NotNull BaseComponent[] getComponent(@NotNull String key) {
    return BungeeUtils.getComponent(BungeeUtils.build(this.get(key)));
  }

  /**
   * Get the component from a given key and placeholders
   *
   * @param key the key to get from the file and turn it into component
   * @param placeholders the placeholders of the component
   * @return the component from the given key
   */
  public @NotNull BaseComponent[] getComponent(
      @NotNull String key, @NotNull Map<String, String> placeholders) {
    return BungeeUtils.getComponent(BungeeUtils.build(this.get(key, placeholders)));
  }

  /**
   * Get the component from a given key and placeholders
   *
   * @param key the key to get from the file and turn it into component
   * @param placeholders the placeholders in the component
   * @return the component from the given key
   */
  public @NotNull BaseComponent[] getComponent(
      @NotNull String key, @NotNull MapBuilder<String, String> placeholders) {
    return BungeeUtils.getComponent(BungeeUtils.build(this.get(key, placeholders)));
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
    return this.config.getString(s);
  }

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @Override
  public @NotNull String get(@NotNull String path) {
    String raw = this.getRaw(path);
    return raw == null ? path : raw;
  }
}
