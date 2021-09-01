package me.googas.guido.commands;

import me.googas.commands.annotations.Multiple;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;

public class FunCommands {

  @Command(
      aliases = "status",
      description = "Change the status of the Bot",
      permission = Permission.ADMINISTRATOR)
  public Result status(
      JDA jda,
      @Required(name = "status", description = "The new status to set") OnlineStatus status,
      @Required(name = "activity", description = "The new activity to be doing")
          Activity.ActivityType activity,
      @Multiple @Required(name = "description", description = "The new status description to set")
          String string) {
    jda.getPresence().setPresence(status, Activity.of(activity, string));
    return null;
  }
}
