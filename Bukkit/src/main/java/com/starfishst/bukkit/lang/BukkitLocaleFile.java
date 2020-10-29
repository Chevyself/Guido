package com.starfishst.bukkit.lang;

import me.googas.api.lang.LocaleFile;
import java.io.File;
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
  public void setLang(@NotNull String s) {
    throw new UnsupportedOperationException("Language cannot be changed in locale files");
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

  @Override
  public @NotNull File getFile() {
    throw new UnsupportedOperationException("Bukkit locale files do not use files");
  }
}
