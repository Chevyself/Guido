package com.starfishst.bukkit.lang;

import com.starfishst.bukkit.AnnotatedCommand;
import com.starfishst.bukkit.ParentCommand;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.messages.MessagesProvider;
import com.starfishst.core.arguments.Argument;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** Handles the language for messages */
public class BukkitLanguageHandler implements GuidoListener, MessagesProvider {

  /** The loaded locale files */
  @NonNull private final List<BukkitLocaleFile> files = new ArrayList<>();

  /**
   * Load the locale files as resources
   *
   * @param plugin the plugin to load the files
   * @param langs the languages to load
   * @return this same instance
   */
  public BukkitLanguageHandler loadResources(@NonNull Plugin plugin, @NonNull String... langs) {
    for (String lang : langs) {
      InputStream resource = plugin.getResource("lang/" + lang + ".yml");
      InputStreamReader reader = new InputStreamReader(resource);
      try {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(reader);
        this.files.add(new BukkitLocaleFile(configuration));
      } catch (IOException | InvalidConfigurationException e) {
        Fallback.addError(lang + " could not be loaded");
        e.printStackTrace();
      }
      plugin.getLogger().info(lang + ".yml has been loaded");
    }
    return this;
  }

  /**
   * Get the placeholders for a command
   *
   * @param cmd the command to get the placeholders from
   * @return the placeholders as a builder
   */
  @NonNull
  private MapBuilder<String, String> getCommandPlaceholders(@NonNull AnnotatedCommand cmd) {
    return Maps.builder("name", cmd.getName())
        .append("aliases", Lots.pretty(cmd.getAliases()))
        .append("description", cmd.getDescription())
        .append("permission", cmd.getDescription());
  }

  /**
   * Get the locale file for certain locale
   *
   * @param locale the locale to get the file from
   * @return the file
   */
  @NonNull
  private BukkitLocaleFile getFile(@NonNull String locale) {
    for (BukkitLocaleFile localeFile : this.files) {
      if (localeFile.getLang().equalsIgnoreCase(locale)) {
        return localeFile;
      }
    }
    return this.getFile("en");
  }

  /**
   * Get the placeholders for an argument
   *
   * @param argument the argument to get the placeholders to
   * @return the placeholders
   */
  @NonNull
  private MapBuilder<String, String> getArgumentPlaceholders(@NonNull Argument<?> argument) {
    return Maps.builder("name", argument.getName())
        .append("description", argument.getDescription())
        .append("position", String.valueOf(argument.getPosition()));
  }

  /**
   * Get the file for certain sender
   *
   * @param sender the sender to get the language then the file
   * @return the file or 'en' if not found
   */
  @NonNull
  public BukkitLocaleFile getFile(@NonNull CommandSender sender) {
    return this.getFile(
        sender instanceof Player ? ((Player) sender).spigot().getLocale().split("_")[0] : "en");
  }

  /**
   * Get the file for certain command context
   *
   * @param context the context to get the sender
   * @return the language of the context or 'en' by default
   */
  @NonNull
  public BukkitLocaleFile getFile(@NonNull CommandContext context) {
    return this.getFile(context.getSender());
  }

  /**
   * Send a localized message to all players
   *
   * @param key the key of the message
   * @param placeholders the placeholders of the message
   */
  public void broadcast(@NonNull String key, @NonNull Map<String, String> placeholders) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(this.getFile(player).get(key, placeholders));
    }
  }

  /**
   * Get the default locale file
   *
   * @return the default locale file which is 'en'
   */
  @NonNull
  private BukkitLocaleFile getDefault() {
    return this.getFile("en");
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
  public @NonNull String invalidNumber(@NonNull String s, @NonNull CommandContext context) {
    return this.getFile(context).get("invalid.number", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String emptyDouble(@NonNull CommandContext context) {
    return this.getFile(context).get("invalid.decimal-empty");
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
  public @NonNull String playersOnly(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("player-only");
  }

  @Override
  public @NonNull String notAllowed(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  public @NonNull String helpTopicShort(@NonNull Plugin plugin) {
    return this.getDefault()
        .get(
            "help-topic.plugin.short",
            Maps.builder("name", plugin.getName())
                .append("description", plugin.getDescription().getDescription())
                .append("version", plugin.getDescription().getVersion()));
  }

  @Override
  public @NonNull String helpTopicFull(
      @NonNull String s, @NonNull String s1, @NonNull Plugin plugin) {
    return this.getDefault()
        .get(
            "help-topic.plugin.full",
            Maps.builder("name", plugin.getName())
                .append("description", plugin.getDescription().getDescription())
                .append("version", plugin.getDescription().getVersion())
                .append("short", s)
                .append("commands", s1));
  }

  @Override
  public @NonNull String helpTopicCommand(@NonNull AnnotatedCommand cmd) {
    return this.getDefault().get("help-topic.plugin.command", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NonNull String commandShortText(@NonNull AnnotatedCommand cmd) {
    return this.getDefault().get("help-topic.command.short", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NonNull String commandName(AnnotatedCommand cmd) {
    return this.getDefault().get("help-topic.command.name", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NonNull String parentCommandFull(
      @NonNull ParentCommand parentCommand,
      @NonNull String s,
      @NonNull String s1,
      @NonNull String s2) {
    return this.getDefault()
        .get(
            "help-topic.parent.full",
            this.getCommandPlaceholders(parentCommand)
                .append("short", s)
                .append("children", s1)
                .append("arguments", s2));
  }

  @Override
  public @NonNull String parentCommandShort(
      @NonNull ParentCommand parentCommand, @NonNull String s) {
    return this.getDefault()
        .get("help-topic.parent.short", this.getCommandPlaceholders(parentCommand));
  }

  @Override
  public @NonNull String commandFull(
      @NonNull AnnotatedCommand annotatedCommand, @NonNull String s, @NonNull String s1) {
    return this.getDefault()
        .get(
            "help-topic.command.full",
            this.getCommandPlaceholders(annotatedCommand).append("short", s).append("usage", s1));
  }

  @Override
  public @NonNull String childCommandName(
      @NonNull AnnotatedCommand cmd, @NonNull ParentCommand parentCommand) {
    return this.getDefault().get("help-topic.parent.child.name", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NonNull String childCommandShort(
      @NonNull AnnotatedCommand annotatedCommand, @NonNull ParentCommand parentCommand) {
    return this.getDefault()
        .get("help-topic.parent.child.short", this.getCommandPlaceholders(annotatedCommand));
  }

  @Override
  public @NonNull String childCommandFull(
      @NonNull AnnotatedCommand annotatedCommand,
      @NonNull ParentCommand parentCommand,
      @NonNull String s,
      @NonNull String s1) {
    return this.getDefault()
        .get("help-topic.parent.child.full", this.getCommandPlaceholders(annotatedCommand));
  }

  @Override
  public @NonNull String requiredArgumentHelp(@NonNull Argument<?> argument) {
    return this.getDefault()
        .get("help-topic.arguments.required", this.getArgumentPlaceholders(argument));
  }

  @Override
  public @NonNull String optionalArgumentHelp(@NonNull Argument<?> argument) {
    return this.getDefault()
        .get("help-topic.arguments.optional", this.getArgumentPlaceholders(argument));
  }

  @Override
  public @NonNull String childCommand(
      @NonNull AnnotatedCommand annotatedCommand, @NonNull ParentCommand parentCommand) {
    return this.getDefault()
        .get(
            "help-topic.plugin.child",
            this.getCommandPlaceholders(annotatedCommand)
                .append("parent-name", parentCommand.getName()));
  }

  @Override
  public @NonNull String invalidMaterialEmpty(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.material-empty");
  }

  @Override
  public @NonNull String invalidMaterial(
      @NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.material", Maps.singleton("string", s));
  }

  @Override
  public void onUnload() {
    this.files.clear();
  }

  @Override
  public @NonNull String getName() {
    return "language";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
