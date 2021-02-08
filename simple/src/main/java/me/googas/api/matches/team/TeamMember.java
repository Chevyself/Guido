package me.googas.api.matches.team;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;

/** Represents the linked info inside a team */
public class TeamMember {

  @NonNull @Getter private final LinkableInfo link;
  @NonNull @Getter private final TeamRole role;

  public TeamMember(@NonNull LinkableInfo link, @NonNull TeamRole role) {
    this.link = link;
    this.role = role;
  }

  /** @deprecated this constructor may only be used by gson */
  public TeamMember() {
    this(new LinkableInfo(), TeamRole.NORMAL);
  }
}
