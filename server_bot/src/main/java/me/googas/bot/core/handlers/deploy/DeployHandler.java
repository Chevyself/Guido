package me.googas.bot.core.handlers.deploy;

import me.googas.bot.core.handlers.GuidoHandler;

/**
 * The deploy handler is in charge to inform other services on changes such a punishment being done
 * or a permission being added or removed
 */
public class DeployHandler implements GuidoHandler {

  /* TODO send localized messages
  @Listener(priority = ListenPriority.HIGHEST)
  public void onLinkableEloUpdated(LinkableEloUpdatedEvent event) {
    if (event.isWinner()) {
      event
          .getData()
          .sendLocalized(
              "elo.updated.winner",
              Maps.builder("old", String.valueOf(event.getPrevious()))
                  .put("new", String.valueOf(event.getNewElo()))
                  .put("ladder", event.getLadder().getName()));
    } else {
      event
          .getData()
          .sendLocalized(
              "elo.updated.loser",
              Maps.builder("old", String.valueOf(event.getPrevious()))
                  .put("new", String.valueOf(event.getNewElo()))
                  .put("ladder", event.getLadder().getName()));
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void LinkableRankUpdated(LinkableRankUpdatedEvent event) {
    RanksHandler.UpdateResult update = event.getUpdate();
    event
        .getData()
        .sendLocalized(
            "elo.rank.updated",
            Maps.builder("applied", Ranks.getRanksToken(update.getApplied()))
                .put("removed", Ranks.getRanksToken(update.getRemoved())));
  }*/

  @Override
  public void onDisable() {}
}
