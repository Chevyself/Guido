package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.arguments.Argument;
import com.starfishst.core.arguments.ISimpleArgument;
import com.starfishst.jda.AnnotatedCommand;
import com.starfishst.jda.CommandManager;
import com.starfishst.jda.ParentCommand;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import me.googas.api.lang.LocaleFile;
import me.googas.bot.api.Guido;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;

/** Command for helping people use the bot */
public class HelpCommand {

  /** The arguments builder for commands */
  final StringBuilder argsBuilder = new StringBuilder();

  /**
   * Get help for all the commands
   *
   * @param locale the locale of the sender
   * @param context the context of the command
   * @param cmdName the name of the command to see
   * @return help for the command or every command
   */
  @Command(
      aliases = {"help", "ayuda", "commands", "", "?"},
      description = "help.desc")
  public Result help(
      LocaleFile locale,
      CommandContext context,
      @Optional(name = "help.cmd", description = "help.cmd.desc") String cmdName) {
    CommandManager commandManager = Guido.getCommandManager();
    if (cmdName != null) {
      AnnotatedCommand command = commandManager.getCommand(cmdName);
      if (command != null) {
        if (command.hasPermission(context)) {
          StringBuilder builder = Strings.getBuilder();
          String node = command.getPermission().getNode();
          builder.append(
              locale.get(
                  "help.cmd.title",
                  Maps.builder("name", command.getName())
                      .append("desc", locale.get(command.getDescription()))
                      .append("aliases", Lots.pretty(command.getAliases()))
                      .append("perm", node.isEmpty() ? locale.get("help.cmd.empty-node") : node)));
          this.argsBuilder.setLength(0);
          for (ISimpleArgument<?> argument : command.getArguments()) {
            if (argument instanceof Argument<?>) {
              if (((Argument<?>) argument).isRequired()) {
                this.argsBuilder.append(
                    locale.get(
                        "help.cmd.arg.required",
                        Maps.builder("name", locale.get(((Argument<?>) argument).getName()))));
              } else {
                this.argsBuilder.append(
                    locale.get(
                        "help.cmd.arg.optional",
                        Maps.builder("name", locale.get(((Argument<?>) argument).getName()))));
              }
            }
          }
          builder.append(
              locale.get(
                  "help.cmd.usage",
                  Maps.builder("arguments", this.argsBuilder.toString())
                      .append("name", command.getName())));
          if (command instanceof ParentCommand && command.hasPermission(context)) {
            this.argsBuilder.setLength(0);
            for (AnnotatedCommand child : ((ParentCommand) command).getCommands()) {
              this.argsBuilder.append(
                  locale.get(
                      "help.parent-cmd.child",
                      Maps.builder("name", child.getName())
                          .append("desc", locale.get(child.getDescription()))));
            }
            builder.append(
                locale.get(
                    "help.parent.children",
                    Maps.singleton("children", this.argsBuilder.toString())));
          }
          return new Result(builder.toString());
        } else {
          return new Result(
              ResultType.PERMISSION,
              locale.get("help.not-allowed", Maps.singleton("name", cmdName)));
        }
      } else {
        return new Result(
            ResultType.USAGE, locale.get("help.unknown-cmd", Maps.singleton("name", cmdName)));
      }
    }
    StringBuilder builder = Strings.getBuilder();
    builder.append(locale.get("help.cmds.title"));
    for (AnnotatedCommand command : commandManager.getCommands()) {
      if (command.hasPermission(context)) {
        builder.append(
            locale.get(
                "help.cmds",
                Maps.builder("name", command.getName())
                    .append("desc", locale.get(command.getDescription()))));
      }
    }
    return new Result(builder.toString());
  }
}
