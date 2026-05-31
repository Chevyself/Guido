package me.googas.bot.core.handlers.deploy;

import me.googas.api.Requests;
import me.googas.api.events.links.LinkableEloUpdatedEvent;
import me.googas.api.events.permissible.PermissiblePermissionAddedEvent;
import me.googas.api.events.permissible.PermissiblePermissionRemovedEvent;
import me.googas.api.links.Linkable;
import me.googas.api.permissions.Permissible;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.links.LinkableRankUpdatedEvent;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.ranks.RanksHandler;
import me.googas.bot.core.util.Ranks;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;

/**
 * The deploy handler is in charge to inform other services on changes such a punishment being done
 * or a permission being added or removed
 */
public class DeployHandler implements GuidoHandler {

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
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onPermissiblePermissionAdded(PermissiblePermissionAddedEvent event) {
    Permissible permissible = event.getPermissible();
    if (permissible instanceof Linkable) {
      Requests.Deploy.addPermission(
              ((Linkable) permissible).getInfo(),
              event.getContext(),
              event.getNode(),
              event.isEnabled(),
              event.getExpires())
          .queue(Guido.getServer());
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onPermissiblePermissionRemoved(PermissiblePermissionRemovedEvent event) {
    Permissible permissible = event.getPermissible();
    if (permissible instanceof Linkable) {
      Requests.Deploy.removePermission(
              ((Linkable) permissible).getInfo(), event.getContext(), event.getNode())
          .queue(Guido.getServer());
    }
  }

  @Override
  public void onDisable() {}
}
