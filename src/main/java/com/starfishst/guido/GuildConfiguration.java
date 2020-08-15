package com.starfishst.guido;

import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.core.utils.time.Time;
import com.starfishst.utils.gson.GsonProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The configuration for each guild */
public class GuildConfiguration extends Catchable {

  /** The responsive messages that this config uses */
  @Nullable private List<ResponsiveMessage> messages;
  /** The guild that owns this configuration */
  @Nullable private Guild guild;

  /** Create the guild configuration */
  public GuildConfiguration() {
    super(Time.fromString("10m"));
  }

  /**
   * Get the configuration for a guild
   *
   * @param guild the guild to get the configuration from
   * @return the guild configuration
   */
  @NotNull
  public static GuildConfiguration getConfiguration(@NotNull Guild guild) {
    return (GuildConfiguration)
        Cache.getCache().stream()
            .filter(
                catchable -> {
                  if (catchable instanceof GuildConfiguration) {
                    Guild catchedGuild = ((GuildConfiguration) catchable).guild;
                    if (catchedGuild != null) {
                      return catchedGuild.equals(guild);
                    }
                  }
                  return false;
                })
            .findFirst()
            .orElseGet(
                () -> {
                  File file =
                      CoreFiles.getFile(
                          CoreFiles.currentDirectory() + "/guilds/" + guild.getId() + ".json");
                  if (file != null) {
                    try {
                      FileReader reader = new FileReader(file);
                      return GsonProvider.GSON.fromJson(reader, GuildConfiguration.class);
                    } catch (FileNotFoundException e) {
                      e.printStackTrace();
                      return new GuildConfiguration()
                          .setGuild(guild)
                          .setMessages(new ArrayList<>());
                    }
                  } else {
                    return new GuildConfiguration().setGuild(guild).setMessages(new ArrayList<>());
                  }
                });
  }

  /**
   * Set the list of responsive messages
   *
   * @param messages the new list of responsive messages
   * @return the guild configuration
   */
  @NotNull
  public GuildConfiguration setMessages(List<ResponsiveMessage> messages) {
    this.messages = messages;
    return this;
  }

  /**
   * Set the guild that this configuration uses
   *
   * @param guild the new configuration that this uses
   * @return the guild configuration
   */
  @NotNull
  public GuildConfiguration setGuild(Guild guild) {
    this.guild = guild;
    return this;
  }

  /**
   * Get the list of responsive messages
   *
   * @return the list of responsive messages
   */
  @NotNull
  public List<ResponsiveMessage> getMessages() {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    return this.messages;
  }

  /**
   * Get the guild that this configuration is from
   *
   * @return the guild that this configuration is from
   */
  @Nullable
  public Guild getGuild() {
    return this.guild;
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    if (this.guild != null) {
      try {
        FileWriter writer =
            new FileWriter(
                CoreFiles.getOrCreate(
                    CoreFiles.currentDirectory() + "/guilds/", this.guild.getId() + ".json"));
        GsonProvider.GSON.toJson(this, writer);
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
