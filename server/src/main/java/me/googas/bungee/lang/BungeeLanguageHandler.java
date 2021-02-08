package me.googas.bungee.lang;

import com.starfishst.commands.bungee.context.CommandContext;
import com.starfishst.commands.bungee.messages.MessagesProvider;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.NonNull;
import me.googas.bungee.events.GuidoListener;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/** The language handler for bungee */
public class BungeeLanguageHandler implements GuidoListener, MessagesProvider {

  /** The loaded locale files */
  @NonNull private final List<BungeeLocaleFile> files = new ArrayList<>();

  /**
   * Load the locale files as resources
   *
   * @param plugin the plugin to load the files
   * @param langs the languages to load
   * @return this same instance
   */
  public BungeeLanguageHandler loadResources(@NonNull Plugin plugin, @NonNull String... langs) {
    for (String lang : langs) {
      InputStream resource = plugin.getResourceAsStream("lang/" + lang + ".yml");
      Configuration config =
          ConfigurationProvider.getProvider(YamlConfiguration.class).load(resource);
      this.files.add(new BungeeLocaleFile(config));
      plugin.getLogger().info(lang + ".yml has been loaded");
    }
    plugin.getLogger().info("Languages from resources have been loaded");
    return this;
  }

  /**
   * Get the file for certain locale or 'en' in case it is not found
   *
   * @param locale the locale to getId the file to
   * @return the file for the locale
   */
  @NonNull
  public BungeeLocaleFile getFile(@NonNull String locale) {
    for (BungeeLocaleFile file : this.files) {
      if (file.getLang().equalsIgnoreCase(locale)) {
        return file;
      }
    }
    return this.getFile("en");
  }

  /**
   * Get the file for certain command sender
   *
   * @param sender the sender of the command
   * @return the locale file for the sender
   */
  @NonNull
  public BungeeLocaleFile getFile(@NonNull CommandSender sender) {
    if (!(sender instanceof ProxiedPlayer)) return this.getFile("en");
    Locale locale = ((ProxiedPlayer) sender).getLocale();
    if (locale == null) return this.getFile("en");
    return this.getFile(locale.toString().split("_")[0]);
  }

  /**
   * Get the file for certain command context
   *
   * @param context the context to getId the file from
   * @return the locale file of the context
   */
  @NonNull
  public BungeeLocaleFile getFile(@NonNull CommandContext context) {
    return this.getFile(context.getSender());
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
    return this.getFile(context).get("invalid.decimal", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidBoolean(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.boolean", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String invalidTime(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.time", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String missingArgument(
      @NonNull String s, @NonNull String s1, int i, CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", s1)
                .append("description", s1)
                .append("position", String.valueOf(i)));
  }

  @Override
  public @NonNull String missingStrings(
      @NonNull String s,
      @NonNull String s1,
      int i,
      int i1,
      int i2,
      @NonNull CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", s1)
                .append("description", s1)
                .append("position", String.valueOf(i)));
  }

  @Override
  public @NonNull String invalidPlayer(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid-player", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String onlyPlayers(CommandContext commandContext) {
    return this.getFile(commandContext).get("player-only");
  }

  @Override
  public @NonNull String notAllowed(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  public void onUnload() {
    this.files.clear();
  }

  @Override
  public @NonNull String getName() {
    return "language";
  }
}
