package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.bot.api.Guido;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.bot.core.util.Mongo;
import me.googas.starbox.CoreFiles;

// TODO localize
public class GroupManagementCommands {

  @Parent
  @GuidoJdaPermission("user:guido.group-manager")
  @Command(
      aliases = {"groupManager", "gm"},
      description = "Manages groups")
  public Result groupManager() {
    return Result.of("Group manager is used to easily edit groups thru its files");
  }

  @GuidoJdaPermission("user:guido.group-manager.list")
  @Command(aliases = "list", description = "Lists all the ids of the groups")
  public Result list() {
    List<String> ids = new ArrayList<>();
    for (Group group : Guido.getHandlers().getLoader().getGroups().getGroups()) {
      ids.add(group.getId());
    }
    return Result.of(ids.toString());
  }

  @GuidoJdaPermission("user:guido.group-manager.new")
  @Command(aliases = "new", description = "Creates a file for a new group")
  public Result newGroup() {
    Group group =
        new Group(
                Guido.getHandlers().getLoader().getGroups().nextGroupId(),
                new ArrayList<>(),
                new HashMap<>(),
                new HashSet<>(),
                "new group",
                0)
            .cache();
    return this.save(group);
  }

  @GuidoJdaPermission("user:guido.group-manager.save")
  @Command(aliases = "save", description = "Saves the group to a file in groups/<name>.json")
  public Result save(
      @Required(name = "Group", description = "The group to save to file") Group group) {
    FileWriter writer;
    try {
      File file =
          CoreFiles.getOrCreate(
              CoreFiles.currentDirectory() + "/groups/", group.getName() + ".json");
      writer = new FileWriter(file, false);
      Mongo.GSON.toJson(group, writer);
    } catch (IOException e) {
      e.printStackTrace();
      return Result.of(group.getName() + ".json could not be created due to an IOException.");
    }
    boolean error = false;
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      error = true;
    }
    String message = "Group has been saved to " + group.getName() + ".json";
    if (error)
      message =
          message + "\n" + "**WARNING** Writer could not be properly closed, please check console.";
    return Result.of(message);
  }

  @GuidoJdaPermission("user:guido.group-manager.load")
  @Command(aliases = "load", description = "Loads a group and overrides it if it already exists")
  public Result load(
      @Required(name = "File", description = "The file to load the group of") String fileName) {
    File file = CoreFiles.getFile(CoreFiles.currentDirectory() + "/groups", fileName);
    if (file == null)
      return Result.of("File " + fileName + " could not be found inside the `groups` directory");
    FileReader reader;
    Group group;
    try {
      reader = new FileReader(file);
      group = Mongo.GSON.fromJson(reader, Group.class);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return Result.of("Could not open to read file " + fileName);
    }
    boolean error = false;
    boolean overridden = false;
    try {
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
      error = true;
    }
    GroupLoader groupLoader = Guido.getHandlers().getLoader().getGroups();
    Group old = groupLoader.getGroup(group.getId());
    if (old != null) {
      if (groupLoader.deleteGroup(group.getId())) overridden = true;
    }
    group.cache();
    String message = "The group " + group.getName() + " has been saved";
    if (overridden) {
      message = message + "\n" + " As there was a group with the same id it has been overridden";
    }
    if (error) {
      message =
          message
              + "\n"
              + "**WARNING** The writer could not be closed properly please check the console for information";
    }
    return Result.of(message);
  }
}
