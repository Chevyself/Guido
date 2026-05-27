package me.googas.bot.core.commands.administrative;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import me.googas.api.utility.Lots;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.responsive.roles.GiveRoleReactionResponse;
import me.googas.bot.core.handlers.responsive.roles.GiveRoleResponsiveMessage;
import me.googas.bot.core.util.Discord;
import me.googas.bungee.commands.middleware.GuidoJdaPermission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/** Commands given for administrators */
public class AdministrationCommands {

  @GuidoJdaPermission("guido.giver")
  @Command(aliases = "giver", description = "Make the message given a role")
  public Result giver(
      Message message,
      GuidoGuild guild,
      TextChannel channel,
      @Required(name = "id", description = "The id of the message thatt will give the role")
          long id,
      @Required(name = "role", description = "The role to give") Role role,
      @Free(
              name = "unicode",
              description = "The unicode which the user has to react to oin the queue")
          String unicode) {
    String unicodeToUse;
    if (message.getReactions().isEmpty()) {
      if (unicode == null) {
        return Result.of("If you are not using an emote please include an unicode");
      } else {
        unicodeToUse = unicode;
      }
    } else {
      unicodeToUse = message.getReactions().get(0).getEmoji().getName();
    }
    channel
        .retrieveMessageById(id)
        .queue(
            msg -> {
              if (msg != null) {
                GiveRoleReactionResponse reactionResponse =
                    new GiveRoleReactionResponse(role.getIdLong(), unicodeToUse);
                ResponsiveMesage responsive = guild.getMessage(msg.getIdLong());
                if (responsive instanceof GiveRoleResponsiveMessage) {
                  responsive.addReactionResponse(reactionResponse);
                } else {
                  guild
                      .getMessages()
                      .add(new GiveRoleResponsiveMessage(msg, Lots.set(reactionResponse)));
                }
              }
            },
            Discord.exceptionConsumer());
    return Result.of("Creating...");
  }
}
