package me.googas.api.loader;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.DiscordLinkable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public interface DiscordLinkableLoader extends DataLoader {

  @NonNull
  DiscordLinkable ensureByUser(@NonNull User user);

  @NonNull
  default DiscordLinkable ensureByMember(@NonNull Member member) {
    return this.ensureByUser(member.getUser());
  }

  @NonNull
  Optional<? extends DiscordLinkable> getByLinkedUser(@NonNull UUID linkedUserId);
}
