package me.googas.bot;

import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.context.GuildCommandContext;
import com.github.chevyself.starbox.jda.messages.JdaMessagesProvider;
import com.github.chevyself.starbox.metadata.CommandMetadata;
import com.github.chevyself.starbox.middleware.Middleware;
import com.github.chevyself.starbox.result.Result;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.bot.api.DiscordLoader;
import me.googas.bot.core.discord.GuidoRole;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements Middleware<CommandContext> {

  /**
   * The set of developers which are allowed to use any command without having the respective
   * permission
   */
  @NonNull private final Set<Long> developers = Lots.set(86321059636203520L);

  @NonNull private final JdaMessagesProvider messagesProvider;
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
      @NonNull JdaMessagesProvider messagesProvider,
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
  public boolean checkMemberPermission(@NonNull GuildCommandContext context, @NonNull String perm) {
    Member discordMember = context.getMember();
    Guild guild = context.getGuild();
    // discordMember.getIdLong(), guild.getIdLong()
    DiscordLinkable member = Discord.getUser(discordMember);
    if (member.hasPermission(perm, "discord")
        || discordMember.hasPermission(Permission.ADMINISTRATOR)) {
      return true;
    }
    for (Role role : discordMember.getRoles()) {
      GuidoRole roleData = this.discordLoader.getRole(role.getIdLong());
      if (roleData.hasPermission(perm, "discord") || role.hasPermission(Permission.ADMINISTRATOR)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @NonNull Optional<Result> next(@NonNull CommandContext context) {
    CommandMetadata metadata = context.getCommand().getMetadata();
    if (!metadata.has(GuidoMetadataParser.PERMISSION_KEY)) return Optional.empty();
    String permissionNode = metadata.get(GuidoMetadataParser.PERMISSION_KEY);
    if (permissionNode.isEmpty()) return Optional.empty();
    if (this.developers.contains(context.getSender().getIdLong())) return Optional.empty();
    if (context instanceof GuildCommandContext) {
      if (this.checkMemberPermission((GuildCommandContext) context, permissionNode)) {
        return Optional.empty();
      }
    } else {
      String node =
          permissionNode.startsWith("user:") ? permissionNode.substring(5) : permissionNode;
      Linkable userData =
          this.dataLoader
              .getLinks()
              .getLink(LinkableType.DISCORD, Maps.singleton("id", context.getSender().getIdLong()));
      if (userData != null && userData.hasPermission(node, "discord")) {
        return Optional.empty();
      }
    }
    Result result = Result.of(this.messagesProvider.notAllowed(context));
    return Optional.of(result);
  }
}
