package me.googas.bot.core.commands;

import com.starfishst.commands.jda.annotations.Command;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.commands.jda.result.ResultType;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
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
import me.googas.bot.core.util.Mongo;
import me.googas.commons.CoreFiles;

// TODO localize
public class GroupManagementCommands {

  @Parent
  @Command(
      aliases = {"groupManager", "gm"},
      description = "Manages groups",
      node = "user:guido.group-manager")
  public Result groupManager() {
    return new Result("Group manager is used to easily edit groups thru its files");
  }

  @Command(
      aliases = "list",
      description = "Lists all the ids of the groups",
      node = "user:guido.group-manager.list")
  public Result list() {
    List<String> ids = new ArrayList<>();
    for (Group group : Guido.getHandlers().getLoader().getGroups().getGroups()) {
      ids.add(group.getId());
    }
    return new Result(ids.toString());
  }

  @Command(
      aliases = "new",
      description = "Creates a file for a new group",
      node = "user:guido.group-manager.new")
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

  @Command(
      aliases = "save",
      description = "Saves the group to a file in groups/<name>.json",
      node = "user:guido.group-manager.save")
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
      return new Result(
          ResultType.ERROR, group.getName() + ".json could not be created due to an IOException.");
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
    return new Result(message);
  }

  @Command(
      aliases = "load",
      description = "Loads a group and overrides it if it already exists",
      node = "user:guido.group-manager.load")
  public Result load(
      @Required(name = "File", description = "The file to load the group of") String fileName) {
    File file = CoreFiles.getFile(CoreFiles.currentDirectory() + "/groups", fileName);
    if (file == null)
      return new Result(
          ResultType.ERROR,
          "File " + fileName + " could not be found inside the `groups` directory");
    FileReader reader;
    Group group;
    try {
      reader = new FileReader(file);
      group = Mongo.GSON.fromJson(reader, Group.class);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return new Result(ResultType.ERROR, "Could not open to read file " + fileName);
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
    return new Result(message);
  }
}
