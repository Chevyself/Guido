package me.googas.bot.api.events.data.links;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.links.LinkableEvent;
import me.googas.api.links.Linkable;
import me.googas.bot.core.handlers.ranks.RanksHandler;

/** Called when the ranks of a linkable gets updated */
public class LinkableRankUpdatedEvent extends LinkableEvent {

  @NonNull @Getter private final RanksHandler.UpdateResult update;

  public LinkableRankUpdatedEvent(
      @NonNull Linkable linkable, RanksHandler.@NonNull UpdateResult update) {
    super(linkable);
    this.update = update;
  }
}
