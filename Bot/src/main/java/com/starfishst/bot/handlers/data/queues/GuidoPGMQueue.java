package com.starfishst.bot.handlers.data.queues;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoLinkedValuesMap;
import com.starfishst.bot.handlers.data.GuidoMatch;
import com.starfishst.bot.handlers.data.GuidoTeam;
import com.starfishst.bot.util.console.Console;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.googas.commons.Lots;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A queue that uses pgm */
public class GuidoPGMQueue extends GuidoQueue {

  /**
   * Create the queue
   *
   * @param guildId the id of the guild where the queue is happening
   * @param ladder the names of the ladders waiting for queue
   */
  public GuidoPGMQueue(long guildId, @NotNull String ladder) {
    super(guildId, ladder);
  }

  @Override
  @Nullable
  public GuidoMatch checkReady() {
    Ladder ladder = this.getLadder();
    if (this.getWaiting().size() >= ladder.playersPerTeam()) {
      Map<LinkedInfo, TeamRole> participants = new HashMap<>();
      for (int i = 0; i < ladder.playersPerTeam(); i++) {
        participants.put(this.getWaiting().get(i), TeamRole.NORMAL);
      }
      this.getWaiting().removeAll(participants.keySet());
      return new GuidoMatch(
          Lots.set(new GuidoTeam(participants, "participants")),
          new GuidoLinkedValuesMap("type", "pgm").addValue("ladder", ladder.getName()));
    }
    return null;
  }

  @Override
  public boolean join(@NotNull LinkedInfo info) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    LinkedData data = info.getData();
    if (bungee != null && data != null) {
      UserData user = data.getLinkedUser();
      if (user != null) {
        Collection<LinkedData> links =
            Guido.getDataLoader().getLinks(user, LinkedDataType.MINECRAFT);
        if (!links.isEmpty()) {
          UUID uuid = null;
          LinkedInfo toPlay = null;
          for (LinkedData link : links) {
            String trimmed = link.getIdentification().getValue("uuid", String.class);
            if (trimmed != null) {
              uuid = UUIDUtils.untrim(trimmed);
              toPlay = link.getInfo();
              break;
            }
          }
          if (uuid != null) {
            try {
              Boolean bol =
                  bungee.sendRequest(
                      new Request<>(Boolean.class, "is-online", Maps.singleton("uuid", uuid)));
              if (bol != null) {
                if (bol) {
                  bungee.sendRequest(
                      new Request<>(Boolean.class, "add-queue", Maps.singleton("uuid", uuid)),
                      ignored -> {});
                  super.join(toPlay);
                }
                return bol;
              }
            } catch (MessengerListenFailException e) {
              Console.exception(e, "Failed while checking if player is online");
            }
          }
          throw new IllegalStateException(
              "Some of the minecraft links of "
                  + user
                  + " is wrong as it does not have uuid or an invalid one!");
        } else {
          //  TODO TELL THEM TO LINK MINECRAFT
        }
      }
    }
    return false;
  }

  @Override
  public boolean leave(@NotNull LinkedInfo data) {
    if (super.leave(data)) {
      JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
      LinkedData linked = data.getData();
      if (bungee != null && linked != null) {
        String trimmed = linked.getIdentification().getValue("uuid", String.class);
        if (trimmed != null) {
          try {
            bungee.sendRequest(
                new Request<>(
                    Boolean.class,
                    "remove-queue",
                    Maps.singleton("uuid", UUIDUtils.untrim(trimmed))),
                bol -> {});
          } catch (IllegalArgumentException ignored) {
          }
        }
      }
    }
    return false;
  }
}
