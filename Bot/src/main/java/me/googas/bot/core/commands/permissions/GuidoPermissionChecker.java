package me.googas.bot.core.commands.permissions;

import com.starfishst.jda.PermissionChecker;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.messages.MessagesProvider;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.Set;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.api.types.BotRole;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

  /**
   * IF the permission is applicable as a guild context this will check if the user has the
   * permission as a member
   *
   * @param context the context of the command must be inside a guild
   * @param perm the permission to check
   * @return if this is true the user has the permission
   */
  public boolean checkMemberPermission(@NotNull GuildCommandContext context, @NotNull Perm perm) {
    Member discordMember = context.getMember();
    Guild guild = context.getGuild();
    BotLinkableData member =
        this.dataLoader.getMemberData(discordMember.getIdLong(), guild.getIdLong());
    if (member.hasPermission(perm.node(), "discord")
        || discordMember.hasPermission(Permission.ADMINISTRATOR)
        || (perm.permission() != Permission.UNKNOWN
            && discordMember.hasPermission(perm.permission()))) {
      return true;
    }
    for (Role role : discordMember.getRoles()) {
      BotRole roleData = this.dataLoader.getRoleData(role.getIdLong(), guild.getIdLong());
      if (roleData.hasPermission(perm.node(), "discord")
          || role.hasPermission(Permission.ADMINISTRATOR)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @Nullable Result checkPermission(@NotNull CommandContext context, @NotNull Perm perm) {
    if (!perm.node().isEmpty()) {
      if (this.developers.contains(context.getSender().getIdLong())) {
        return null;
      }
      if (context instanceof GuildCommandContext) {
        if (this.checkMemberPermission((GuildCommandContext) context, perm)) {
          return null;
        }
      } else {
        String node = perm.node().startsWith("user:") ? perm.node().substring(5) : perm.node();
        BotLinkableData userData =
            this.dataLoader.getDiscordUserData(context.getSender().getIdLong());
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
