package com.starfishst.guido.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.commands.utils.embeds.EmbedFactory;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.core.utils.Strings;
import com.starfishst.guido.GuildConfiguration;
import com.starfishst.guido.responsive.role.RoleGiver;
import com.starfishst.guido.responsive.role.RoleInformation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
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
      permission = Permission.ADMINISTRATOR,
      description = "Evalua codigo JavaScript")
  public Result eval(
      GuildCommandContext context,
      @Multiple @Required(name = "code", description = "El codigo para analizar")
          JoinedStrings strings) {
    this.engine.put("event", context.getEvent());
    this.engine.put("message", context.getMessage());
    this.engine.put("channel", context.getChannel());
    this.engine.put("args", context.getStrings());
    this.engine.put("api", this.api);
    this.engine.put("guild", context.getGuild());
    this.engine.put("member", context.getMember());

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

  @Command(
      aliases = "giver",
      description = "Crea un mensaje para dar rangos",
      permission = Permission.ADMINISTRATOR)
  public Result roleGiver(
      Message message,
      @Multiple
          @Required(
              name = "parametros",
              description = "Los parametros especificando la descripcion y el unciode del rol")
          JoinedStrings strings) {
    String joinedString = strings.getString();
    if (!joinedString.contains(":")) {
      return new Result(ResultType.USAGE, "No hay `:` separando la informacion de cada rol");
    } else {
      List<Role> mentionedRoles = message.getMentionedRoles();
      String[] split = joinedString.split(":");
      if (split.length < 2) {
        return new Result(ResultType.USAGE, "No hay informacion del rol");
      }
      String[] rolesInfo = split[1].split(",");
      if (mentionedRoles.size() != rolesInfo.length) {
        return new Result(
            ResultType.USAGE,
            "Hay una cantidad diferente de roles e informacion! Hay "
                + mentionedRoles.size()
                + " roles y "
                + rolesInfo.length
                + " informacion de ellos!");
      } else {
        StringBuilder descBuilder = Strings.getBuilder();
        LinkedHashMap<String, Role> roleHashMap = new LinkedHashMap<>();
        for (int i = 0; i < rolesInfo.length; i++) {
          String roleInfo = rolesInfo[i];
          if (!roleInfo.contains(";")) {
            return new Result(ResultType.USAGE, "No hay `;` separando la descripcion del unicode!");
          } else {
            String[] unicodeNDesc = roleInfo.split(";");
            String unicode = unicodeNDesc[0].replace(" ", "");
            String desc = unicodeNDesc[1];
            roleHashMap.put(unicode, mentionedRoles.get(i));
            descBuilder.append(desc).append("\n");
          }
        }
        return new Result(
            EmbedFactory.newEmbed(
                "Reclama un rol",
                descBuilder.toString(),
                null,
                null,
                "Radiator Springs",
                null,
                null,
                false),
            msg -> {
              RoleGiver roleGiver = new RoleGiver(msg.getIdLong());
              List<RoleInformation> rolesInformation = new ArrayList<>();
              roleHashMap.forEach(
                  (unicode, role) -> {
                    rolesInformation.add(new RoleInformation(roleGiver, unicode, role.getIdLong()));
                    msg.addReaction(unicode).queue();
                  });
              roleGiver.addRolesInformation(rolesInformation);
              GuildConfiguration configuration =
                  GuildConfiguration.getConfiguration(message.getGuild());
              configuration.getMessages().add(roleGiver);
            });
      }
    }
  }
}
