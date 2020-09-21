package com.starfishst.bot.handlers.data.loader;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.bot.api.events.data.member.BotMemberUnloadedEvent;
import com.starfishst.bot.api.events.data.role.BotRoleUnloadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.bot.handlers.data.GuidoGuild;
import com.starfishst.bot.handlers.data.GuidoMember;
import com.starfishst.bot.handlers.data.GuidoRole;
import com.starfishst.bot.handlers.data.GuidoUser;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import com.starfishst.utils.gson.GsonProvider;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/**
 * This loader will attempt to get the data from files if it fails it will create a new instance of
 * the data required
 */
public class GuidoFileLoader implements BotDataLoader, GuidoEventHandler {
  /**
   * This will listen to when the guild data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NotNull BotGuildUnloadedEvent event) {
    try {
      File file =
          CoreFiles.getOrCreate(
              CoreFiles.currentDirectory() + "/data/" + event.getData().getId() + "/info.json");
      FileWriter writer = new FileWriter(file);
      GsonProvider.GSON.toJson(event.getData(), writer);
      writer.close();
    } catch (IOException e) {
      Fallback.addError(
          "IOException: Data for guild " + event.getData().getId() + " could not be saved");
      e.printStackTrace();
    }
  }

  /**
   * This will listen to when the member data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMemberDataUnloaded(@NotNull BotMemberUnloadedEvent event) {
    try {
      File file =
          CoreFiles.getOrCreate(
              CoreFiles.currentDirectory()
                  + "/data/"
                  + event.getData().getGuildId()
                  + "/members/"
                  + event.getData().getId()
                  + ".json");
      FileWriter writer = new FileWriter(file);
      GsonProvider.GSON.toJson(event.getData(), writer);
      writer.close();
    } catch (IOException e) {
      Fallback.addError(
          "IOException: Data for member "
              + event.getData().getId()
              + " in guild "
              + event.getData().getGuildId()
              + " could not be saved");
      e.printStackTrace();
    }
  }

  /**
   * This will listen to when the role data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleDataUnloaded(@NotNull BotRoleUnloadedEvent event) {
    try {
      File file =
          CoreFiles.getOrCreate(
              CoreFiles.currentDirectory()
                  + "/data/"
                  + event.getData().getGuildId()
                  + "/roles/"
                  + event.getData().getId()
                  + ".json");
      FileWriter writer = new FileWriter(file);
      GsonProvider.GSON.toJson(event.getData(), writer);
      writer.close();
    } catch (IOException e) {
      Fallback.addError(
          "IOException: Data for role "
              + event.getData().getId()
              + " in guild "
              + event.getData().getGuildId()
              + " could not be saved");
      e.printStackTrace();
    }
  }

  /**
   * This will listen to when the user data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onUserDataUnloaded(@NotNull BotUserUnloadedEvent event) {
    try {
      File file =
          CoreFiles.getOrCreate(
              CoreFiles.currentDirectory() + "/users/" + event.getData().getId() + ".json");
      FileWriter writer = new FileWriter(file);
      GsonProvider.GSON.toJson(event.getData(), writer);
      writer.close();
    } catch (IOException e) {
      Fallback.addError(
          "IOException: Data for user " + event.getData().getId() + " could not be saved");
      e.printStackTrace();
    }
  }

  /**
   * Load the data of a guild
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @Override
  public @NotNull BotGuild getGuildData(long id) {
    GuidoGuild guild =
        Cache.getCatchable(
            catchable -> catchable instanceof GuidoGuild && ((GuidoGuild) catchable).getId() == id,
            GuidoGuild.class);
    if (guild != null) {
      return guild;
    }
    File file = CoreFiles.getFile(CoreFiles.currentDirectory() + "/data/" + id + "/info.json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        BotGuild data = GsonProvider.GSON.fromJson(reader, GuidoGuild.class);
        reader.close();
        return data;
      } catch (IOException e) {
        Fallback.addError(
            "IOException: Data for guild " + id + " could not be loaded. Created a fallback");
        e.printStackTrace();
        return new GuidoGuild(id);
      }
    } else {
      return new GuidoGuild(id);
    }
  }

  /**
   * Load the data of a member
   *
   * @param id the id of the member
   * @param guildId the guild id from which the data of the member must be gotten
   * @return the data of the member or null if not found
   */
  @Override
  public @NotNull BotMember getMemberData(long id, long guildId) {
    GuidoMember member =
        Cache.getCatchable(
            catchable ->
                catchable instanceof GuidoMember
                    && ((GuidoMember) catchable).getId() == id
                    && ((GuidoMember) catchable).getGuildId() == guildId,
            GuidoMember.class);
    if (member != null) {
      return member;
    }
    File file =
        CoreFiles.getFile(
            CoreFiles.currentDirectory() + "/data/" + guildId + "/members/" + id + ".json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        BotMember data = GsonProvider.GSON.fromJson(reader, GuidoMember.class);
        reader.close();
        return data;
      } catch (IOException e) {
        Fallback.addError(
            "IOException: Data for member "
                + id
                + " from guild "
                + guildId
                + " could not be loaded. Created a fallback");
        e.printStackTrace();
        return new GuidoMember(id, guildId, new HashSet<>(), new HashMap<>(), new HashMap<>());
      }
    } else {
      return new GuidoMember(id, guildId, new HashSet<>(), new HashMap<>(), new HashMap<>());
    }
  }

  /**
   * Load the data of a role
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    GuidoRole role =
        Cache.getCatchable(
            catchable ->
                catchable instanceof GuidoRole
                    && ((GuidoRole) catchable).getId() == id
                    && ((GuidoRole) catchable).getGuildId() == guildId,
            GuidoRole.class);
    if (role != null) {
      return role;
    }
    File file =
        CoreFiles.getFile(
            CoreFiles.currentDirectory() + "/data/" + guildId + "/roles/" + id + ".json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        BotRole data = GsonProvider.GSON.fromJson(reader, GuidoRole.class);
        reader.close();
        return data;
      } catch (IOException e) {
        Fallback.addError(
            "IOException: Data for role "
                + id
                + " from guild "
                + guildId
                + " could not be loaded. Created a fallback");
        e.printStackTrace();
        return new GuidoRole(id, guildId, new HashSet<>());
      }
    } else {
      return new GuidoRole(id, guildId, new HashSet<>());
    }
  }

  @Override
  public @NotNull BotUser getUserData(long id) {
    GuidoUser user =
        Cache.getCatchable(
            catchable -> catchable instanceof GuidoUser && ((GuidoUser) catchable).getId() == id,
            GuidoUser.class);
    if (user != null) {
      return user;
    }
    File file = CoreFiles.getFile(CoreFiles.currentDirectory() + "/users/" + id + ".json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        BotUser data = GsonProvider.GSON.fromJson(reader, GuidoUser.class);
        reader.close();
        return data;
      } catch (IOException e) {
        Fallback.addError(
            "IOException: Data for user " + id + " could not be loaded. Created a fallback");
        e.printStackTrace();
        return new GuidoUser(id, "en", new HashSet<>());
      }
    } else {
      return new GuidoUser(id, "en", new HashSet<>());
    }
  }
}
