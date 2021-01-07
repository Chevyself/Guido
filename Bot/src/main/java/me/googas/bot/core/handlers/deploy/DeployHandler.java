package me.googas.bot.core.handlers.deploy;

import me.googas.api.links.Linkable;
import me.googas.api.permissions.Permissible;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.links.LinkableEloUpdatedEvent;
import me.googas.bot.api.events.data.links.LinkableRankUpdatedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.ranks.RanksHandler;
import me.googas.bot.core.util.Ranks;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;

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
                  .append("new", String.valueOf(event.getNewElo()))
                  .append("ladder", event.getLadder().getName()));
    } else {
      event
          .getData()
          .sendLocalized(
              "elo.updated.loser",
              Maps.builder("old", String.valueOf(event.getPrevious()))
                  .append("new", String.valueOf(event.getNewElo()))
                  .append("ladder", event.getLadder().getName()));
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
                .append("removed", Ranks.getRanksToken(update.getRemoved())));
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onPermissiblePermissionAdded(PermissiblePermissionAddedEvent event) {
    Permissible permissible = event.getPermissible();
    if (permissible instanceof Linkable) {
      Guido.getServer()
          .sendRequest(
              new Request<>(
                  Boolean.class,
                  "server/add-permission",
                  Maps.objects("info", ((Linkable) permissible).getInfo())
                      .append("node", event.getNode())
                      .append("context", event.getContext())
                      .append("expires", event.getExpires())
                      .build()),
              (messenger, added) -> {
                // TODO nothing at the moment
              });
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onPermissiblePermissionRemoved(PermissiblePermissionRemovedEvent event) {
    Permissible permissible = event.getPermissible();
    if (permissible instanceof Linkable) {
      Guido.getServer()
          .sendRequest(
              new Request<>(
                  Boolean.class,
                  "server/remove-permission",
                  Maps.objects("info", ((Linkable) permissible).getInfo())
                      .append("node", event.getNode())
                      .append("context", event.getContext())
                      .build()),
              (messenger, added) -> {
                // TODO nothing at the moment
              });
    }
  }

  @Override
  public void onDisable() {}
}
