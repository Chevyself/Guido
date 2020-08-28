package com.starfishst.guido;

import com.starfishst.commands.PermissionChecker;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.commands.messages.MessagesProvider;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.RoleData;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.data.loader.GuidoFileLoader;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements PermissionChecker {

  /** The messages provider in case that it has to remove a result */
  @NotNull private final MessagesProvider messagesProvider;

  /**
   * The file loader to get the permissions
   */
  @NotNull private final GuidoFileLoader guidoFileLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param guidoFileLoader the file loader to get the permissions from the user
   */
  public GuidoPermissionChecker(@NotNull MessagesProvider messagesProvider, @NotNull GuidoFileLoader guidoFileLoader) {
    this.messagesProvider = messagesProvider;
    this.guidoFileLoader = guidoFileLoader;
  }

  @Override
  public @Nullable Result checkPermission(@NotNull CommandContext context, @NotNull Perm perm) {
    if (perm.permission() != Permission.UNKNOWN) {
      return new Result(ResultType.ERROR, "This operation is not supported by this bot");
    }
    if (!perm.node().isEmpty()) {
      if (context instanceof GuildCommandContext) {
        MemberData member = this.guidoFileLoader.getMemberData(((GuildCommandContext) context).getMember().getIdLong(), ((GuildCommandContext) context).getGuild());
        if (member.hasPermission(perm.node())){
          return null;
        }
        for (Role role : ((GuildCommandContext) context).getMember().getRoles()) {
          RoleData roleData = this.guidoFileLoader.getRoleData(role.getIdLong(), ((GuildCommandContext) context).getGuild());
          if (roleData.hasPermission(perm.node())) {
            return null;
          }
        }
      } else {
        UserData userData = this.guidoFileLoader.getUserData(context.getSender().getIdLong());
        if (userData.hasPermission(perm.node())) {
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
