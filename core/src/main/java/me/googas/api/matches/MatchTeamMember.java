package me.googas.api.matches;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableMatcher;
import me.googas.api.matches.team.TeamRole;

public interface MatchTeamMember {

  @NonNull
  TeamRole getRole();

  @NonNull
  Optional<? extends Linkable> getLinkable(@NonNull LinkableMatcher linkableMatcher);
}
