package me.googas.api.links;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.loader.Loader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/** This interface represents a linkable Minecraft account */
public interface DiscordLinkable extends Linkable {

  long getId();

  default User getUser(@NonNull JDA jda) {
    return jda.getUserById(this.getId());
  }

  default Optional<Member> getMember(@NonNull JdaProvider jdaProvider) {
    return Optional.ofNullable(jdaProvider.getGuild().getMemberById(this.getId()));
  }

  @Override
  default @NonNull String getPublicDisplayName(@NonNull Loader loader) {
    return this.getLinkedUserId()
        .flatMap(linkedUserId -> loader.getMinecraftLinks().getByLinkedUser(linkedUserId))
        .map(MinecraftLinkable::getNickname)
        .orElseGet(
            () -> {
              // TODO actual member name
              String effectiveName = getId() + "";
              return effectiveName.contains(" - ") ? effectiveName.split(" - ")[1] : effectiveName;
            });
  }
}
