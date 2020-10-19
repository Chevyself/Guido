package com.starfishst.bungee.core.lang;

import com.starfishst.guido.api.data.lang.LocaleFile;
import java.io.File;
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

  @Override
  public void setLang(@NotNull String s) {
    throw new UnsupportedOperationException("Bungee locale does not support setting the file lang");
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

  @Override
  public @NotNull File getFile() {
    throw new UnsupportedOperationException("Bungee locale does not support files");
  }
}
