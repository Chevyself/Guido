package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import me.googas.api.discord.GuildData;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotResponsiveMessage;
import me.googas.bot.core.handlers.responsive.roles.GiveRoleReactionResponse;
import me.googas.bot.core.handlers.responsive.roles.GiveRoleResponsiveMessage;
import me.googas.bot.core.util.Discord;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands given for administrators */
public class AdministrationCommands {

  /**
   * Create a message that gives a role
   *
   * @param message the message that executed the command
   * @param guild the guild where the command was executed
   * @param channel the channel where the command was executed
   * @param id the id of the message to become a responsive message
   * @param role the role to give in the message
   * @param unicode the unicode the user has to use to get the role
   * @return whether the message was created
   */
  @Command(
      aliases = "giver",
      description = "Make the message given a role",
      permission = @Perm(node = "guido.giver"))
  public Result giver(
      Message message,
      GuildData guild,
      TextChannel channel,
      @Required(name = "id", description = "The id of the message thatt will give the role")
          long id,
      @Required(name = "role", description = "The role to give") Role role,
      @Optional(
              name = "unicode",
              description = "The unicode which the user has to react to oin the queue")
          String unicode) {
    String unicodeToUse;
    if (message.getEmotes().isEmpty()) {
      if (unicode == null) {
        return new Result(
            ResultType.USAGE, "If you are not using an emote please include an unicode");
      } else {
        unicodeToUse = unicode;
      }
    } else {
      unicodeToUse = message.getEmotes().get(0).getName();
    }
    if (!(guild instanceof BotGuild))
      return new Result(ResultType.ERROR, "Guild is not bot guild (Internal Error)");
    channel
        .retrieveMessageById(id)
        .queue(
            msg -> {
              if (msg != null) {
                GiveRoleReactionResponse reactionResponse =
                    new GiveRoleReactionResponse(role.getIdLong(), unicodeToUse);
                BotResponsiveMessage responsive = ((BotGuild) guild).getMessage(msg.getIdLong());
                if (responsive instanceof GiveRoleResponsiveMessage) {
                  responsive.addReactionResponse(reactionResponse);
                } else {
                  ((BotGuild) guild)
                      .getMessages()
                      .add(new GiveRoleResponsiveMessage(msg, Lots.set(reactionResponse)));
                }
              }
            },
            Discord.exceptionConsumer());
    return new Result("Creating...");
  }
}
