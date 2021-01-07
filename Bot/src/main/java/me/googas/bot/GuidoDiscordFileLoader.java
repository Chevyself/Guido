package me.googas.bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.bot.api.DiscordLoader;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.discord.GuidoRole;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.CoreFiles;

public class GuidoDiscordFileLoader implements GuidoHandler, DiscordLoader {

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
                if (guild != null) return guild;
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
                  new HashSet<>());
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
                if (role != null) return role;
              } catch (IOException e) {
                e.printStackTrace();
              }
              return new GuidoRole(roleId, new HashSet<>());
            });
  }

  @Override
  public void onDisable() {}
}
