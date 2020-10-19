package com.starfishst.bot;

import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.jda.PermissionChecker;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.Set;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements PermissionChecker {

  /**
   * The set of developers which are allowed to use any command without having the respective
   * permission
   */
  @NotNull private final Set<Long> developers = Lots.set(86321059636203520L);

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
    // If a node starts with user: it will check for user permissions not member
    if (!perm.node().isEmpty()) {
      if (this.developers.contains(context.getSender().getIdLong())) {
        return null;
      }
      if (context instanceof GuildCommandContext && !perm.node().startsWith("user:")) {
        BotLinkedData member =
            this.dataLoader
                .getMemberData(
                    ((GuildCommandContext) context).getMember().getIdLong(),
                    ((GuildCommandContext) context).getGuild().getIdLong())
                .refresh();
        if (member.hasPermission(perm.node(), "discord")) {
          return null;
        }
        for (Role role : ((GuildCommandContext) context).getMember().getRoles()) {
          BotRole roleData =
              this.dataLoader
                  .getRoleData(
                      role.getIdLong(), ((GuildCommandContext) context).getGuild().getIdLong())
                  .refresh();
          if (roleData.hasPermission(perm.node(), "discord")) {
            return null;
          }
        }
      } else {
        String node = perm.node().startsWith("user:") ? perm.node().substring(5) : perm.node();
        BotLinkedData userData =
            this.dataLoader.getDiscordUserData(context.getSender().getIdLong()).refresh();
        if (userData.hasPermission(node, "discord")) {
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
