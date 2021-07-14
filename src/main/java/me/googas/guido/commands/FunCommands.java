package me.googas.guido.commands;

import me.googas.commands.annotations.Multiple;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.objects.JoinedStrings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

public class FunCommands {

  @Command(
      aliases = "status",
      description = "Change the status of the Bot",
      permission = Permission.ADMINISTRATOR)
  public Result status(
      JDA jda,
      @Multiple @Required(name = "status", description = "The new status to set")
          JoinedStrings strings) {
    // jda.getPresence().setPresence(Activity.);
    return new Result();
  }
}
