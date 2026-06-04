package me.googas.api.matches.minecraft;

import java.util.Set;
import lombok.NonNull;

public interface MinecraftMatchTeam {

  /** An id to differentiate the teams in a match. -1 if not set for some reason */
  int getId();

  @NonNull
  Set<? extends MinecraftMatchTeamMember> getMembers();

  @NonNull
  String getName();
}
