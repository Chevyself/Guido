package com.starfishst.bukkit.lang;

import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.messages.BukkitMessagesProvider;
import com.starfishst.bukkit.modules.GuidoModule;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.starbox.builders.MapBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** Handles the language for messages */
public class BukkitLanguageHandler implements GuidoModule, BukkitMessagesProvider {

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
        e.printStackTrace();
      }
      plugin.getLogger().info(lang + ".yml has been loaded");
    }
    return this;
  }

  /**
   * Get the placeholders for a command
   *
   * @param cmd the command to getId the placeholders from
   * @return the placeholders as a builder
   */
  @NonNull
  private MapBuilder<String, String> getCommandPlaceholders(@NonNull BukkitCommand cmd) {
    return Maps.builder("name", cmd.getName())
        .put("aliases", Lots.pretty(cmd.getAliases()))
        .put("description", cmd.getExecutor().getDescription())
        .put("permission", cmd.getPermission());
  }

  /**
   * Get the locale file for certain locale
   *
   * @param locale the locale to getId the file from
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
   * Get the file for certain sender
   *
   * @param sender the sender to getId the language then the file
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
   * @param context the context to getId the sender
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
      player.sendMessage(this.getFile((CommandSender) player).get(key, placeholders));
    }
  }

  @NonNull
  public BukkitLocaleFile getFile(@NonNull Player player) {
    return this.getFile(player.spigot().getLocale().split("_")[0]);
  }

  @NonNull
  public BukkitLocaleFile getFile(@NonNull OfflinePlayer player) {
    Player onlinePlayer = player.getPlayer();
    if (onlinePlayer != null) return this.getFile((CommandSender) onlinePlayer);
    return this.getDefault();
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
  public @NonNull String invalidDuration(
      @NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid.duration", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String missingArgument(
      @NonNull String s, @NonNull String s1, int i, CommandContext context) {
    return this.getFile(context)
        .get(
            "missing-argument",
            Maps.builder("name", s1).put("description", s1).put("position", String.valueOf(i)));
  }

  @Override
  public @NonNull String cooldown(
      @NonNull CommandContext commandContext, @NonNull Duration duration) {
    return this.getFile(commandContext).get("cooldown");
  }

  @Override
  public @NonNull String invalidPlayer(@NonNull String s, @NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("invalid-player", Maps.singleton("string", s));
  }

  @Override
  public @NonNull String onlyPlayers(@NonNull CommandContext commandContext) {
    return this.getFile(commandContext).get("only-players");
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
                .put("description", plugin.getDescription().getDescription())
                .put("version", plugin.getDescription().getVersion()));
  }

  @Override
  public @NonNull String helpTopicFull(
      @NonNull String s, @NonNull String s1, @NonNull Plugin plugin) {
    return this.getDefault()
        .get(
            "help-topic.plugin.full",
            Maps.builder("name", plugin.getName())
                .put("description", plugin.getDescription().getDescription())
                .put("version", plugin.getDescription().getVersion())
                .put("short", s)
                .put("commands", s1));
  }

  @Override
  public @NonNull String helpTopicCommand(@NonNull BukkitCommand BukkitCommand) {
    return this.getDefault()
        .get("help-topic.plugin.command", this.getCommandPlaceholders(BukkitCommand));
  }

  @Override
  public @NonNull String commandShortText(@NonNull BukkitCommand BukkitCommand) {
    return this.getDefault()
        .get("help-topic.command.short", this.getCommandPlaceholders(BukkitCommand));
  }

  @Override
  public @NonNull String commandName(BukkitCommand BukkitCommand, String s) {
    return this.getDefault()
        .get("help-topic.command.name", this.getCommandPlaceholders(BukkitCommand));
  }

  @Override
  public @NonNull String commandFullText(@NonNull BukkitCommand BukkitCommand, @NonNull String s) {
    return this.getDefault()
        .get("help-topic.command.full", this.getCommandPlaceholders(BukkitCommand).put("usage", s));
  }

  @Override
  public @NonNull String childCommand(
      @NonNull BukkitCommand BukkitCommand, @NonNull BukkitCommand BukkitCommand1) {
    return this.getDefault()
        .get(
            "help-topic.plugin.child",
            this.getCommandPlaceholders(BukkitCommand)
                .put("parent-name", BukkitCommand1.getName()));
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
  public void onDisable() {
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
