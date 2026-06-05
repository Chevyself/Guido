package me.googas.api.matches.minecraft;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.LinkableMatcher;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MatchTeamMember;

public interface MinecraftMatchTeamMember extends MatchTeamMember {

  @NonNull
  UUID getId();

  @NonNull
  Optional<MinecraftLinkable> getLinkable(@NonNull LinkableMatcher linkableMatcher);
}
