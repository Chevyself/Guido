package me.googas.bot;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.context.GuildCommandContext;
import com.starfishst.commands.jda.messages.MessagesProvider;
import com.starfishst.commands.jda.permissions.JdaPermission;
import com.starfishst.commands.jda.permissions.PermissionChecker;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.commands.jda.result.ResultType;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.bot.api.DiscordLoader;
import me.googas.bot.core.discord.GuidoRole;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Discord;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements PermissionChecker {

  /**
   * The set of developers which are allowed to use any command without having the respective
   * permission
   */
  @NonNull private final Set<Long> developers = Lots.set(86321059636203520L);

  @NonNull private final MessagesProvider messagesProvider;
  @NonNull private final GuidoLoader dataLoader;
  @NonNull private final DiscordLoader discordLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param dataLoader the data loader to getId the permissions from the user
   * @param discordLoader the loader to getId roles
   */
  public GuidoPermissionChecker(
      @NonNull MessagesProvider messagesProvider,
      @NonNull GuidoLoader dataLoader,
      @NonNull DiscordLoader discordLoader) {
    this.messagesProvider = messagesProvider;
    this.dataLoader = dataLoader;
    this.discordLoader = discordLoader;
  }

  /**
   * IF the permission is applicable as a guild context this will check if the user has the
   * permission as a member
   *
   * @param context the context of the command must be inside a guild
   * @param perm the permission to check
   * @return if this is true the user has the permission
   */
  public boolean checkMemberPermission(
      @NonNull GuildCommandContext context, @NonNull JdaPermission perm) {
    Member discordMember = context.getMember();
    Guild guild = context.getGuild();
    // discordMember.getIdLong(), guild.getIdLong()
    DiscordLinkable member = Discord.getUser(discordMember);
    if (member.hasPermission(perm.getNode(), "discord")
        || discordMember.hasPermission(Permission.ADMINISTRATOR)
        || (perm.getPermission() != Permission.UNKNOWN
            && discordMember.hasPermission(perm.getPermission()))) {
      return true;
    }
    for (Role role : discordMember.getRoles()) {
      GuidoRole roleData = this.discordLoader.getRole(role.getIdLong());
      if (roleData.hasPermission(perm.getNode(), "discord")
          || role.hasPermission(Permission.ADMINISTRATOR)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Result checkPermission(@NonNull CommandContext context, @NonNull JdaPermission perm) {
    if (!perm.getNode().isEmpty()) {
      if (this.developers.contains(context.getSender().getIdLong())) {
        return null;
      }
      if (context instanceof GuildCommandContext) {
        if (this.checkMemberPermission((GuildCommandContext) context, perm)) {
          return null;
        }
      } else {
        String node =
            perm.getNode().startsWith("user:") ? perm.getNode().substring(5) : perm.getNode();
        Linkable userData =
            this.dataLoader
                .getLinks()
                .getLink(
                    LinkableType.DISCORD, Maps.singleton("id", context.getSender().getIdLong()));
        if (userData != null && userData.hasPermission(node, "discord")) {
          return null;
        }
      }
      return new Result(ResultType.PERMISSION, this.messagesProvider.notAllowed(context));
    }
    return null;
  }

  @Override
  public @NonNull MessagesProvider getMessagesProvider() {
    return this.messagesProvider;
  }
}
