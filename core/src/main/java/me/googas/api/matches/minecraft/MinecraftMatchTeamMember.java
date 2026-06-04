package me.googas.api.matches.minecraft;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.matches.team.TeamRole;

public interface MinecraftMatchTeamMember {

  @NonNull
  UUID getId();

  @NonNull
  TeamRole getRole();
}
