package me.googas.bot.core.lang;

import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.messages.JdaMessagesProvider;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import lombok.CustomLog;
import lombok.NonNull;
import me.googas.IOUtil;
import me.googas.api.lang.LocaleFile;
import me.googas.api.lang.Localized;
import me.googas.api.loader.Loader;
import me.googas.api.utility.Maps;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Discord;
import me.googas.starbox.CoreFiles;

/** Handles the language for guido messages */
@CustomLog
public class GuidoLanguageHandler implements JdaMessagesProvider, GuidoHandler {

  /** The files that this handler is using */
  @NonNull private final Set<GuidoLocaleFile> files = new HashSet<>();

  /** The loader to getId the localization */
  @NonNull private Loader loader;

  /**
   * Create the guido localization handler
   *
   * @param loader the file loader to getId the user data
   */
  public GuidoLanguageHandler(@NonNull Loader loader) {
    this.loader = loader;
  }

  /**
   * Loads the locale files queried
   *
   * @param toLoad the locale files to load
   */
  @NonNull
  public GuidoLanguageHandler load(String... toLoad) {
    for (String lang : toLoad) {
      try {
        this.files.add(
            new GuidoLocaleFile(
                CoreFiles.getFileOrResource(
                    IOUtil.currentDirectory() + "/assets/lang/" + lang + ".properties",
                    CoreFiles.getResource("lang/" + lang + ".properties"))));
      } catch (IOException e) {
        GuidoLanguageHandler.log.log(
            Level.SEVERE, e, () -> "Failed to register " + lang + ".properties");
      }
    }
    return this;
  }

  /**
   * Get the file for certain lang
   *
   * @param lang the lang to getId the file for
   * @return the file from the lang
   */
  @NonNull
  public GuidoLocaleFile getFile(@NonNull String lang) {
    for (GuidoLocaleFile file : this.files) {
      if (file.getLang().equalsIgnoreCase(lang)) {
        return file;
      }
    }
    return this.getFile("en");
  }

  /**
   * Get the file from a unicode
   *
   * @param unicode the unicode to getId the file from
   * @return the file that posses the unicode
   * @throws IllegalArgumentException if the unicode does not match any file
   */
  @NonNull
  public GuidoLocaleFile getFileFromUnicode(@NonNull String unicode) {
    for (GuidoLocaleFile file : this.files) {
      if (file.getUnicode().equalsIgnoreCase(unicode)) {
        return file;
      }
    }
    throw new IllegalArgumentException(unicode + " is not a valid unicode");
  }

  /**
   * Get the file for certain context. This will getId the lang from the context and then the file
   *
   * @param context the context to getId the lang and the file
   * @return the file or "en" if not found
   */
  @NonNull
  public GuidoLocaleFile getFile(CommandContext context) {
    GuidoLocaleFile file;
    if (context == null) {
      file = this.getFile("en");
    } else {
      file = this.getFile(this.getLang(context));
    }
    return file;
  }

  /**
   * Get the language for certain context. This will getId the user and getId its data to load and
   * getId its locale
   *
   * @param context the context to getId the language form
   * @return the locale for the user
   */
  @NonNull
  public String getLang(@NonNull CommandContext context) {
    return Discord.getUser(context.getSender()).getString(null, "lang", "en");
  }

  @NonNull
  public LocaleFile getFile(@NonNull Localized localized) {
    return this.getFile(localized.getLang());
  }

  /**
   * Set the data loader for the language handler
   *
   * @param loader the new data loader
   */
  public void setLoader(@NonNull Loader loader) {
    this.loader = loader;
  }

  @Override
  public void onDisable() {}

  @Override
  public void unregister() {}

  /**
   * Get the default locale file which in this case is english
   *
   * @return the default locale file
   */
  @NonNull
  public LocaleFile getDefault() {
    return this.getFile("en");
  }

  /**
   * Get the files that are loaded
   *
   * @return the files that are loaded
   */
  @NonNull
  public Set<GuidoLocaleFile> getFiles() {
    return this.files;
  }

  @Override
  public @NonNull String invalidLong(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidInteger(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidDouble(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.double", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidBoolean(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.boolean", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidDuration(
      @NonNull String s, @org.jspecify.annotations.NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.duration", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String missingArgument(
      @NonNull String s, @NonNull String s1, int i, CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", this.getFile(context).get(s))
                .put("description", this.getFile(context).get(s1))
                .put("position", String.valueOf(i)));
  }

  @Override
  public @NonNull String cooldown(
      @org.jspecify.annotations.NonNull CommandContext commandContext, @NonNull Duration duration) {
    return this.getFile(commandContext)
        .get(
            "cooldown",
            Maps.singleton("time", String.valueOf(duration.toSeconds()))); // TODO: format the time
  }

  @Override
  public @NonNull String commandNotFound(
      @NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("command-not-found", Maps.builder("name", s));
  }

  @Override
  public @NonNull String notAllowed(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  public @NonNull String guildOnly(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("guild-only");
  }

  @Override
  public @NonNull String invalidUser(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.user", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidMember(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.member", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidRole(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.role", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidTextChannel(String s, CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.channel", Maps.singleton("string", s));
  }

  @Override
  public String noMessage(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("no-message");
  }

  /** Stops the language handler */
  public void stop() {
    for (GuidoLocaleFile file : this.files) {
      file.save();
    }
  }
}
