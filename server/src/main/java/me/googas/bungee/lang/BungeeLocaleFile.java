package me.googas.bungee.lang;

import com.starfishst.commands.bungee.utils.BungeeUtils;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.commons.maps.MapBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.config.Configuration;

/** The implementation for bungee of locale file */
public class BungeeLocaleFile implements LocaleFile {

  /** The configuration to getId the messages from */
  @NonNull private final Configuration config;

  /**
   * Create the bungee locale file
   *
   * @param config the configuration to getId the messages from
   */
  public BungeeLocaleFile(@NonNull Configuration config) {
    this.config = config;
  }

  /**
   * Get the component from a given key
   *
   * @param key the key to getId from the file and turn it into component
   * @return the component from the given key
   */
  public @NonNull BaseComponent[] getComponent(@NonNull String key) {
    return BungeeUtils.getComponent(BungeeUtils.build(this.get(key)));
  }

  /**
   * Get the component from a given key and placeholders
   *
   * @param key the key to getId from the file and turn it into component
   * @param placeholders the placeholders of the component
   * @return the component from the given key
   */
  public @NonNull BaseComponent[] getComponent(
      @NonNull String key, @NonNull Map<String, String> placeholders) {
    return BungeeUtils.getComponent(BungeeUtils.build(this.get(key, placeholders)));
  }

  /**
   * Get the component from a given key and placeholders
   *
   * @param key the key to getId from the file and turn it into component
   * @param placeholders the placeholders in the component
   * @return the component from the given key
   */
  public @NonNull BaseComponent[] getComponent(
      @NonNull String key, @NonNull MapBuilder<String, String> placeholders) {
    return BungeeUtils.getComponent(BungeeUtils.build(this.get(key, placeholders)));
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
    return this.config.getString(s);
  }

  /**
   * Get the string or the path to create it
   *
   * @param path the path to the string
   * @return the string if the path leads to one else the path
   */
  @Override
  public @NonNull String get(@NonNull String path) {
    String raw = this.getRaw(path);
    return raw == null ? path : raw;
  }
}
