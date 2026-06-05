package me.googas.api.matches;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.links.LinkableMatcher;
import me.googas.api.utility.ImmutableCollection;

public interface MatchTeam {
  /** An id to differentiate the teams in a match. -1 if not set for some reason */
  int getId();

  @NonNull
  ImmutableCollection<? extends MatchTeamMember> getMembers();

  @NonNull
  String getName();

  @NonNull
  Collection<String> getMemberPublicDisplay(@NonNull LinkableMatcher linkableMatcher);
}
