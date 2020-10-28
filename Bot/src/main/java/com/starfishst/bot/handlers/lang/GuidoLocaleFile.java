package com.starfishst.bot.handlers.lang;

import com.starfishst.guido.api.data.lang.LocaleFile;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import me.googas.commons.CoreFiles;
import me.googas.commons.Validate;
import me.googas.commons.fallback.Fallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The locale file for the guido bot. It is loaded using {@link java.util.Properties} */
public class GuidoLocaleFile implements LocaleFile {

  /** The actual file that this is using */
  @NotNull private final File file;
  /** The properties used to get the strings */
  @NotNull private final Properties properties = new Properties();

  /**
   * Create the guido locale file
   *
   * @param file the properties file to get the properties
   * @throws IOException in case that the properties file cannot be read
   */
  public GuidoLocaleFile(@NotNull File file) throws IOException {
    this.file = file;
    Reader reader = new FileReader(this.file);
    this.properties.load(reader);
    reader.close();
    this.copyDefaults();
  }

  /** Copies the default (missing keys) to the file. */
  public void copyDefaults() {
    try {
      Properties defaults = new Properties();
      defaults.load(CoreFiles.getResource("lang/" + this.getLang() + ".properties"));
      for (Object key : defaults.keySet()) {
        if (this.properties.get(key) == null) {
          this.properties.put(key, defaults.get(key));
        }
      }
    } catch (IOException e) {
      Fallback.addError("IOException: The defaults for " + this + " could not be saved");
      e.printStackTrace();
    }
  }

  /**
   * Get the unicode that differentiates this language
   *
   * @return the unicode to differentiate this language
   */
  public @NotNull String getUnicode() {
    return Validate.notNull(this.getRaw("unicode"), this + " has a null unicode property");
  }

  @Override
  public void setLang(@NotNull String lang) {
    throw new UnsupportedOperationException("You cannot change the language from locale files");
  }

  @Override
  public @NotNull String getLang() {
    return Validate.notNull(this.getRaw("lang"), this + " has a null lang property!");
  }

  @Override
  public void save() {
    try {
      FileWriter writer = new FileWriter(this.file);
      this.properties.store(writer, "No comments");
      writer.close();
    } catch (IOException e) {
      Fallback.addError("IOException: Lang file from " + this + " could not be saved");
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable String getRaw(@NotNull String path) {
    return this.properties.getProperty(path);
  }

  @Override
  public @NotNull File getFile() {
    return this.file;
  }

  @Override
  public String toString() {
    return "GuidoLocaleFile{" + "file=" + this.file + "}";
  }
}
