package me.googas.bot.core.commands;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.arguments.Argument;
import com.github.chevyself.starbox.arguments.SingleArgument;
import com.github.chevyself.starbox.jda.commands.JdaAnnotatedCommand;
import com.github.chevyself.starbox.jda.commands.JdaCommand;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.result.Result;
import me.googas.api.lang.LocaleFile;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;

import java.util.Optional;

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
      @Free(name = "help.cmd", description = "help.cmd.desc") String cmdName) {
    CommandManager<CommandContext, JdaCommand> commandManager = Guido.getCommandManager();
    if (cmdName != null) {
      Optional<JdaCommand> optional = commandManager.getCommand(cmdName);
      if (optional.isPresent()) {
        JdaCommand command = optional.get();
        if (true/* TODO add permission check command.hasPermission(context)*/) {
          StringBuilder builder = new StringBuilder();
          String node = /*command.getPermission().getNode() Related to permission check*/ "";
          builder.append(
              locale.get(
                  "help.cmd.title",
                  Maps.builder("name", command.getName())
                      .put("desc", locale.get(command.getDescription()))
                      .put("aliases", Lots.pretty(command.getAliases()))
                      .put("perm", node.isEmpty() ? locale.get("help.cmd.empty-node") : node)));
          this.argsBuilder.setLength(0);
          if (command instanceof JdaAnnotatedCommand annotated) {
              for (Argument<?> argument : annotated.getArguments()) {
              if (!(argument instanceof SingleArgument<?> single)) continue;
                if (single.isRequired()) {
                this.argsBuilder.append(
                        locale.get(
                                "help.cmd.arg.required",
                                Maps.builder("name", locale.get(single.getName()))));
              } else {
                this.argsBuilder.append(
                        locale.get(
                                "help.cmd.arg.optional",
                                Maps.builder("name", locale.get(single.getName()))));
              }
            }
          }

          builder.append(
              locale.get(
                  "help.cmd.usage",
                  Maps.builder("arguments", this.argsBuilder.toString())
                      .put("name", command.getName())));
          if (!command.getChildren().isEmpty()) {
            this.argsBuilder.setLength(0);
            for (JdaCommand child : command.getChildren()) {
              this.argsBuilder.append(
                  locale.get(
                      "help.parent-cmd.child",
                      Maps.builder("name", child.getName())
                          .put("desc", locale.get(child.getDescription()))));
            }
            builder.append(
                locale.get(
                    "help.parent.children",
                    Maps.singleton("children", this.argsBuilder.toString())));
          }
          return Result.of(builder.toString());
        } else {
          return Result.of(
              locale.get("help.not-allowed", Maps.singleton("name", cmdName)));
        }
      } else {
        return Result.of(locale.get("help.unknown-cmd", Maps.singleton("name", cmdName)));
      }
    }
    StringBuilder builder = new StringBuilder();
    builder.append(locale.get("help.cmds.title"));
    for (JdaCommand command : commandManager.getCommands()) {
      if (/*command.hasPermission(context) TODO add permission check*/ true) {
        builder.append(
            locale.get(
                "help.cmds",
                Maps.builder("name", command.getName())
                    .put("desc", locale.get(command.getDescription()))));
      }
    }
    return Result.of(builder.toString());
  }
}
