package com.starfishst.guido.data.loader;

import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.utils.cache.Cache;
import com.starfishst.core.utils.files.CoreFiles;
import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.RoleData;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.loader.DataLoader;
import com.starfishst.guido.api.events.data.GuildDataUnloadedEvent;
import com.starfishst.guido.api.events.data.MemberDataUnloadedEvent;
import com.starfishst.guido.api.events.data.RoleDataUnloadedEvent;
import com.starfishst.guido.data.GuidoGuild;
import com.starfishst.guido.data.GuidoMember;
import com.starfishst.guido.data.GuidoRole;
import com.starfishst.guido.data.GuidoUser;
import com.starfishst.utils.events.ListenPriority;
import com.starfishst.utils.events.Listener;
import com.starfishst.utils.gson.GsonProvider;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This loader will attempt to get the data from files if it fails it will create a new instance of
 * the data required
 */
public class GuidoFileLoader implements DataLoader {
  /**
   * This will listen to when the guild data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NotNull GuildDataUnloadedEvent event) {
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
  public void onMemberDataUnloaded(@NotNull MemberDataUnloadedEvent event) {
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
  public void onRoleDataUnloaded(@NotNull RoleDataUnloadedEvent event) {
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
   * Load the data of a guild
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @Override
  public @NotNull GuildData getGuildData(long id) {
    GuidoGuild guild = Cache.getCatchable(catchable -> catchable instanceof GuidoGuild && ((GuidoGuild) catchable).getId() == id, GuidoGuild.class);
    if (guild != null) {
      return guild;
    }
    File file = CoreFiles.getFile(CoreFiles.currentDirectory() + "/data/" + id + "/info.json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        GuildData data = GsonProvider.GSON.fromJson(reader, GuidoGuild.class);
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
   * @param guild the guild from which the data of the member must be gotten
   * @return the data of the member or null if not found
   */
  @Override
  public @NotNull MemberData getMemberData(long id, @NotNull Guild guild) {
    GuidoMember member = Cache.getCatchable(catchable -> catchable instanceof GuidoMember && ((GuidoMember) catchable).getId() == id && ((GuidoMember) catchable).getGuildId() == guild.getIdLong(), GuidoMember.class);
    if (member != null) {
      return member;
    }
    File file =
        CoreFiles.getFile(
            CoreFiles.currentDirectory() + "/data/" + guild.getId() + "/members/" + id + ".json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        MemberData data = GsonProvider.GSON.fromJson(reader, GuidoMember.class);
        reader.close();
        return data;
      } catch (IOException e) {
        Fallback.addError(
            "IOException: Data for member "
                + id
                + " from guild "
                + guild.getId()
                + " could not be loaded. Created a fallback");
        e.printStackTrace();
        return new GuidoMember(id, guild.getIdLong(), new HashSet<>(), new HashMap<>());
      }
    } else {
      return new GuidoMember(id, guild.getIdLong(), new HashSet<>(), new HashMap<>());
    }
  }

  /**
   * Load the data of a role
   *
   * @param id the id of the role
   * @param guild the guild from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @Override
  public @NotNull RoleData getRoleData(long id, @NotNull Guild guild) {
    GuidoRole role = Cache.getCatchable(catchable -> catchable instanceof GuidoRole && ((GuidoRole) catchable).getId() == id && ((GuidoRole) catchable).getGuildId() == guild.getIdLong(), GuidoRole.class);
    if (role != null) {
      return role;
    }
    File file =
        CoreFiles.getFile(
            CoreFiles.currentDirectory() + "/data/" + guild.getId() + "/roles/" + id + ".json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        RoleData data = GsonProvider.GSON.fromJson(reader, GuidoRole.class);
        reader.close();
        return data;
      } catch (IOException e) {
        Fallback.addError(
            "IOException: Data for role "
                + id
                + " from guild "
                + guild.getId()
                + " could not be loaded. Created a fallback");
        e.printStackTrace();
        return new GuidoRole(id, guild.getIdLong(), new HashSet<>());
      }
    } else {
      return new GuidoRole(id, guild.getIdLong(), new HashSet<>());
    }
  }

  @Override
  public @NotNull UserData getUserData(long id) {
    GuidoUser user = Cache.getCatchable(catchable -> catchable instanceof GuidoUser && ((GuidoUser) catchable).getId() == id, GuidoUser.class);
    if (user != null) {
      return user;
    }
    File file = CoreFiles.getFile(CoreFiles.currentDirectory() + "/users/" + id + ".json");
    if (file != null) {
      try {
        FileReader reader = new FileReader(file);
        UserData data = GsonProvider.GSON.fromJson(reader, GuidoUser.class);
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
