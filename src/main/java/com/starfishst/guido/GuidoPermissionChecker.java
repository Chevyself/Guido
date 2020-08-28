package com.starfishst.guido;

import com.starfishst.commands.PermissionChecker;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.context.GuildCommandContext;
import com.starfishst.commands.messages.MessagesProvider;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements PermissionChecker {

  /** The messages provider in case that it has to remove a result */
  @NotNull private final MessagesProvider messagesProvider;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   */
  public GuidoPermissionChecker(@NotNull MessagesProvider messagesProvider) {
    this.messagesProvider = messagesProvider;
  }

  @Override
  public @Nullable Result checkPermission(@NotNull CommandContext context, @NotNull Perm perm) {
    if (perm.permission() != Permission.UNKNOWN) {
      return new Result(ResultType.ERROR, "This operation is not supported by this bot");
    }
    if (!perm.node().isEmpty()) {
      if (context instanceof GuildCommandContext) {

      } else {

      }
    }
    return null;
  }

  @Override
  public @NotNull MessagesProvider getMessagesProvider() {
    return this.messagesProvider;
  }
}
