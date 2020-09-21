package com.starfishst.bot;

import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.commands.PermissionChecker;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.commands.messages.MessagesProvider;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements PermissionChecker {

  /** The messages provider in case that it has to remove a result */
  @NotNull private final MessagesProvider messagesProvider;

  /** The data loader to get the permissions */
  @NotNull private final BotDataLoader dataLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param dataLoader the data loader to get the permissions from the user
   */
  public GuidoPermissionChecker(
      @NotNull MessagesProvider messagesProvider, @NotNull BotDataLoader dataLoader) {
    this.messagesProvider = messagesProvider;
    this.dataLoader = dataLoader;
  }

  @Override
  public @Nullable Result checkPermission(@NotNull CommandContext context, @NotNull Perm perm) {
    if (perm.permission() != Permission.UNKNOWN) {
      return new Result(ResultType.ERROR, "This operation is not supported by this bot");
    }
    // If a node starts with user: it will check for user permissions not member
    if (!perm.node().isEmpty()) {
      if (context instanceof GuildCommandContext && !perm.node().startsWith("user:")) {
        BotMember member =
            this.dataLoader.getMemberData(
                ((GuildCommandContext) context).getMember().getIdLong(),
                ((GuildCommandContext) context).getGuild().getIdLong());
        if (member.hasPermission(perm.node(), "discord") || member.hasPermission("*", "discord")) {
          return null;
        }
        for (Role role : ((GuildCommandContext) context).getMember().getRoles()) {
          BotRole roleData =
              this.dataLoader.getRoleData(
                  role.getIdLong(), ((GuildCommandContext) context).getGuild().getIdLong());
          if (roleData.hasPermission(perm.node(), "discord")) {
            return null;
          }
        }
      } else {
        String node = perm.node().startsWith("user:") ? perm.node().substring(5) : perm.node();
        BotUser userData = this.dataLoader.getUserData(context.getSender().getIdLong());
        if (userData.hasPermission(node, "discord") || userData.hasPermission("*", "discord")) {
          return null;
        }
      }
      return new Result(ResultType.PERMISSION, this.messagesProvider.notAllowed(context));
    }
    return null;
  }

  @Override
  public @NotNull MessagesProvider getMessagesProvider() {
    return this.messagesProvider;
  }
}
