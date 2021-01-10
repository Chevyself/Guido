package me.googas.bot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.bot.api.DiscordLoader;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.discord.GuidoRole;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.CoreFiles;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;

public class GuidoDiscordFileLoader implements DiscordLoader {

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotGuildUnloadedEvent(BotGuildUnloadedEvent event) {
    BotGuild guild = event.getData();
    try {
      File file =
          CoreFiles.getOrCreate(CoreFiles.currentDirectory() + "/guilds", guild.getId() + ".json");
      this.save(guild, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onBotRoleUnloadedEvent(BotRoleUnloadedEvent event) {
    BotRole role = event.getData();
    try {
      File file =
          CoreFiles.getOrCreate(CoreFiles.currentDirectory() + "/roles", role.getId() + ".json");
      this.save(role, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void save(@NonNull Object object, @NonNull File file) throws IOException {
    FileWriter writer = new FileWriter(file);
    Mongo.GSON.toJson(object, writer);
    writer.close();
  }

  @NonNull
  public GuidoGuild getGuild(long guildId) {
    return Guido.getCache()
        .getOrSupply(
            GuidoGuild.class,
            guild -> guild.getId() == guildId,
            () -> {
              try {
                File file =
                    CoreFiles.getOrCreate(
                        CoreFiles.currentDirectory() + "/guilds", guildId + ".json");
                FileReader reader = new FileReader(file);
                GuidoGuild guild = Mongo.fromJson(reader, GuidoGuild.class);
                reader.close();
                if (guild != null) return guild.cache();
              } catch (IOException e) {
                e.printStackTrace();
              }
              return new GuidoGuild(
                      guildId,
                      new HashSet<>(),
                      new HashSet<>(),
                      new HashMap<>(),
                      new HashMap<>(),
                      new HashMap<>(),
                      new HashSet<>())
                  .cache();
            });
  }

  @NonNull
  public GuidoRole getRole(long roleId) {
    return Guido.getCache()
        .getOrSupply(
            GuidoRole.class,
            role -> role.getId() == roleId,
            () -> {
              try {
                File file =
                    CoreFiles.getOrCreate(
                        CoreFiles.currentDirectory() + "/roles", roleId + ".json");
                FileReader reader = new FileReader(file);
                GuidoRole role = Mongo.fromJson(reader, GuidoRole.class);
                reader.close();
                if (role != null) return role.cache();
              } catch (IOException e) {
                e.printStackTrace();
              }
              return new GuidoRole(roleId, new HashSet<>()).cache();
            });
  }

  @Override
  public void onDisable() {}
}
