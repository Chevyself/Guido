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
import me.googas.api.utility.Lots;
import me.googas.bot.core.loader.GuidoLoader;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

/** Checks the permissions for the guido bot */
public class GuidoPermissionChecker implements Middleware<CommandContext> {

  /**
   * The set of developers which are allowed to use any command without having the respective
   * permission
   */
  @NonNull private final Set<Long> developers = Lots.set(86321059636203520L);

  @NonNull private final JdaMessagesProvider messagesProvider;
  @NonNull private final GuidoLoader dataLoader;

  /**
   * Create the permission checker
   *
   * @param messagesProvider the messages provider in case it has to return a result
   * @param dataLoader the data loader to getId the permissions from the user
   */
  public GuidoPermissionChecker(
      @NonNull JdaMessagesProvider messagesProvider, @NonNull GuidoLoader dataLoader) {
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
  public boolean checkMemberPermission(@NonNull GuildCommandContext context, @NonNull String perm) {
    Member discordMember = context.getMember();
    return discordMember.hasPermission(Permission.ADMINISTRATOR);
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
      /*
      Linkable userData =
          this.dataLoader
              .getLinks()
              .getLink(LinkableType.DISCORD, Maps.singleton("id", context.getSender().getIdLong()));
      if (userData != null && userData.hasPermission(node, "discord")) {
        return Optional.empty();
      }*/
    }
    Result result = Result.of(this.messagesProvider.notAllowed(context));
    return Optional.of(result);
  }
}
