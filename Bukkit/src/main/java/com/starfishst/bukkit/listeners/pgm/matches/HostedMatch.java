package com.starfishst.bukkit.listeners.pgm.matches;

import java.util.Collection;
import me.googas.api.links.LinkableInfo;
import org.jetbrains.annotations.NotNull;

/** A match that is being hosted */
public class HostedMatch {

  /** The id of the match that is being hosted */
  @NotNull private final String id;

  /** The ladder which is being played */
  @NotNull private final String ladder;

  @NotNull private final Collection<LinkableInfo> participants;

  public HostedMatch(
      @NotNull String id, @NotNull String ladder, @NotNull Collection<LinkableInfo> participants) {
    this.id = id;
    this.ladder = ladder;
    this.participants = participants;
  }
}
