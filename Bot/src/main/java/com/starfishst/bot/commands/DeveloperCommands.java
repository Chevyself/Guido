package com.starfishst.bot.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** Commands made for the developer */
public class DeveloperCommands {

  /** The script engine to use */
  @NotNull private final ScriptEngine engine;

  /** The api to use */
  @NotNull private final JDA api;

  /**
   * Create the developer commands
   *
   * @param api the api to use
   */
  public DeveloperCommands(@NotNull JDA api) {
    this.api = api;
    final ScriptEngineManager manager = new ScriptEngineManager();
    this.engine = manager.getEngineByName("nashorn");
    try {
      final String script =
          "var imports = new JavaImporter("
              + "java.io,"
              + "java.lang,"
              + "java.util,"
              + "Packages.net.vd8tion.jda.api,"
              + "Packages.net.vd8tian.jda.api.entities,"
              + "Packages.net.vd8tion.jda.entities,"
              + "Packages.net.vd8tion.jda.api.entities.impl,"
              + "Packages.net.vd8tion.jda.api.managers,"
              + "Packages.net.vd8tion.jda.api.managers.impl,"
              + "Packages.net.vd8tion.jda.api.api.utils);";
      this.engine.eval(script);
    } catch (final ScriptException e) {
      e.printStackTrace();
    }
  }

  /**
   * Evaluate JS code:
   *
   * <p>event the event message the message args the arguments of the command api the api of the bot
   *
   * @param context the context of the command
   * @param strings the code to analyze
   * @return the result of the code
   */
  @Command(
      aliases = "eval",
      permission = @Perm(node = "user:guido.admin"),
      description = "Evalua codigo JavaScript")
  public Result eval(
      CommandContext context,
      @Multiple @Required(name = "code", description = "El codigo para analizar")
          JoinedStrings strings) {
    this.engine.put("event", context.getEvent());
    this.engine.put("message", context.getMessage());
    this.engine.put("channel", context.getChannel());
    this.engine.put("args", context.getStrings());
    this.engine.put("api", this.api);
    if (context instanceof GuildCommandContext) {
      this.engine.put("guild", ((GuildCommandContext) context).getGuild());
      this.engine.put("member", ((GuildCommandContext) context).getMember());
    }
    final String script =
        "(function() {" + "with (imports) {" + strings.getString() + "}" + "})();";

    final Object out;
    try {
      out = this.engine.eval(script);
    } catch (final ScriptException e) {
      return new Result(ResultType.ERROR, e.getMessage());
    }

    return new Result("Script has been executed " + (out == null ? "" : out.toString()));
  }
}
