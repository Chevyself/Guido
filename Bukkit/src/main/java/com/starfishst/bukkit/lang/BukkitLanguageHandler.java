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
import me.googas.commons.Lots;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** Handles the language for messages */
public class BukkitLanguageHandler implements GuidoListener, MessagesProvider {

  /** The loaded locale files */
  @NotNull private final List<BukkitLocaleFile> files = new ArrayList<>();

  /**
   * Load the locale files as resources
   *
   * @param plugin the plugin to load the files
   * @param langs the languages to load
   * @return this same instance
   */
  public BukkitLanguageHandler loadResources(@NotNull Plugin plugin, @NotNull String... langs) {
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
  @NotNull
  private MapBuilder<String, String> getCommandPlaceholders(@NotNull AnnotatedCommand cmd) {
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
  @NotNull
  private BukkitLocaleFile getFile(@NotNull String locale) {
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
  @NotNull
  private MapBuilder<String, String> getArgumentPlaceholders(@NotNull Argument<?> argument) {
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
  @NotNull
  private BukkitLocaleFile getFile(@NotNull CommandSender sender) {
    return this.getFile(
        sender instanceof Player ? ((Player) sender).spigot().getLocale().split("_")[0] : "en");
  }

  /**
   * Get the file for certain command context
   *
   * @param context the context to get the sender
   * @return the language of the context or 'en' by default
   */
  @NotNull
  public BukkitLocaleFile getFile(@NotNull CommandContext context) {
    return this.getFile(context.getSender());
  }

  /**
   * Get the default locale file
   *
   * @return the default locale file which is 'en'
   */
  @NotNull
  private BukkitLocaleFile getDefault() {
    return this.getFile("en");
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
  public @NotNull String playersOnly(@NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("player-only");
  }

  @Override
  public @NotNull String notAllowed(@NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("not-allowed");
  }

  @Override
  public @NotNull String helpTopicShort(@NotNull Plugin plugin) {
    return this.getDefault()
        .get(
            "help-topic.plugin.short",
            Maps.builder("name", plugin.getName())
                .append("description", plugin.getDescription().getDescription())
                .append("version", plugin.getDescription().getVersion()));
  }

  @Override
  public @NotNull String helpTopicFull(
      @NotNull String s, @NotNull String s1, @NotNull Plugin plugin) {
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
  public @NotNull String helpTopicCommand(@NotNull AnnotatedCommand cmd) {
    return this.getDefault().get("help-topic.plugin.command", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NotNull String commandShortText(@NotNull AnnotatedCommand cmd) {
    return this.getDefault().get("help-topic.command.short", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NotNull String commandName(AnnotatedCommand cmd) {
    return this.getDefault().get("help-topic.command.name", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NotNull String parentCommandFull(
      @NotNull ParentCommand parentCommand,
      @NotNull String s,
      @NotNull String s1,
      @NotNull String s2) {
    return this.getDefault()
        .get(
            "help-topic.parent.full",
            this.getCommandPlaceholders(parentCommand)
                .append("short", s)
                .append("children", s1)
                .append("arguments", s2));
  }

  @Override
  public @NotNull String parentCommandShort(
      @NotNull ParentCommand parentCommand, @NotNull String s) {
    return this.getDefault()
        .get("help-topic.parent.short", this.getCommandPlaceholders(parentCommand));
  }

  @Override
  public @NotNull String commandFull(
      @NotNull AnnotatedCommand annotatedCommand, @NotNull String s, @NotNull String s1) {
    return this.getDefault()
        .get(
            "help-topic.command.full",
            this.getCommandPlaceholders(annotatedCommand).append("short", s).append("usage", s1));
  }

  @Override
  public @NotNull String childCommandName(
      @NotNull AnnotatedCommand cmd, @NotNull ParentCommand parentCommand) {
    return this.getDefault().get("help-topic.parent.child.name", this.getCommandPlaceholders(cmd));
  }

  @Override
  public @NotNull String childCommandShort(
      @NotNull AnnotatedCommand annotatedCommand, @NotNull ParentCommand parentCommand) {
    return this.getDefault()
        .get("help-topic.parent.child.short", this.getCommandPlaceholders(annotatedCommand));
  }

  @Override
  public @NotNull String childCommandFull(
      @NotNull AnnotatedCommand annotatedCommand,
      @NotNull ParentCommand parentCommand,
      @NotNull String s,
      @NotNull String s1) {
    return this.getDefault()
        .get("help-topic.parent.child.full", this.getCommandPlaceholders(annotatedCommand));
  }

  @Override
  public @NotNull String requiredArgumentHelp(@NotNull Argument<?> argument) {
    return this.getDefault()
        .get("help-topic.arguments.required", this.getArgumentPlaceholders(argument));
  }

  @Override
  public @NotNull String optionalArgumentHelp(@NotNull Argument<?> argument) {
    return this.getDefault()
        .get("help-topic.arguments.optional", this.getArgumentPlaceholders(argument));
  }

  @Override
  public @NotNull String childCommand(
      @NotNull AnnotatedCommand annotatedCommand, @NotNull ParentCommand parentCommand) {
    return this.getDefault()
        .get(
            "help-topic.plugin.child",
            this.getCommandPlaceholders(annotatedCommand)
                .append("parent-name", parentCommand.getName()));
  }

  @Override
  public @NotNull String invalidMaterialEmpty(@NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.material-empty");
  }

  @Override
  public @NotNull String invalidMaterial(
      @NotNull String s, @NotNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.material", Maps.singleton("string", s));
  }

  @Override
  public void onUnload() {
    this.files.clear();
  }

  @Override
  public @NotNull String getName() {
    return "language";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
