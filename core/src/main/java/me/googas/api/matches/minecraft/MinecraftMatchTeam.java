package me.googas.api.matches.minecraft;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.Loader;
import me.googas.api.matches.MatchTeam;
import me.googas.api.utility.ImmutableCollection;

public interface MinecraftMatchTeam extends MatchTeam {

  @NonNull
  ImmutableCollection<? extends MinecraftMatchTeamMember> getMembers();

  @NonNull
  default Collection<String> getMemberPublicDisplay(@NonNull Loader loader) {
    return getMembers().stream()
        .map(
            member ->
                member.getLinkable(loader).map(MinecraftLinkable::getNickname).orElse("Uknown"))
        .collect(Collectors.toSet());
  }

  @NonNull
  Optional<String> getPgmPartyId();
}
