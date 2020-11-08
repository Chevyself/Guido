package com.starfishst.bungee.core.lang;

import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.messages.MessagesProvider;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/** The language handler for bungee */
public class BungeeLanguageHandler implements GuidoListener, MessagesProvider {

  /** The loaded locale files */
  @NotNull private final List<BungeeLocaleFile> files = new ArrayList<>();

  /**
   * Load the locale files as resources
   *
   * @param plugin the plugin to load the files
   * @param langs the languages to load
   * @return this same instance
   */
  public BungeeLanguageHandler loadResources(@NotNull Plugin plugin, @NotNull String... langs) {
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
   * @param locale the locale to get the file to
   * @return the file for the locale
   */
  @NotNull
  public BungeeLocaleFile getFile(@NotNull String locale) {
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
  @NotNull
  public BungeeLocaleFile getFile(@NotNull CommandSender sender) {
    return this.getFile(
        sender instanceof ProxiedPlayer
            ? ((ProxiedPlayer) sender).getLocale().toString().split("_")[0]
            : "en");
  }

  /**
   * Get the file for certain command context
   *
   * @param context the context to get the file from
   * @return the locale file of the context
   */
  @NotNull
  public BungeeLocaleFile getFile(@NotNull CommandContext context) {
    return this.getFile(context.getSender());
  }

  @Override
  public @NotNull String invalidLong(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidInteger(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidDouble(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.decimal", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidBoolean(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.boolean", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String invalidTime(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.time", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String missingArgument(
      @NotNull String s, @NotNull String s1, int i, CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", s1)
                .append("description", s1)
                .append("position", String.valueOf(i)));
  }

  @Override
  public @NotNull String invalidNumber(@NotNull String s, @NotNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String emptyDouble(@NotNull CommandContext context) {
    return this.getFile(context).get("invalid.decimal-empty");
  }

  @Override
  public @NotNull String missingStrings(
      @NotNull String s,
      @NotNull String s1,
      int i,
      int i1,
      int i2,
      @NotNull CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", s1)
                .append("description", s1)
                .append("position", String.valueOf(i)));
  }

  @Override
  public @NotNull String invalidPlayer(@NotNull String s, @NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid-player", Maps.singleton("string", s));
  }

  @Override
  public @NotNull String onlyPlayers(CommandContext commandContext) {
    return this.getFile(commandContext).get("player-only");
  }

  @Override
  public @NotNull String notAllowed(@NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  public void onUnload() {
    this.files.clear();
  }

  @Override
  public @NotNull String getName() {
    return "language";
  }
}
