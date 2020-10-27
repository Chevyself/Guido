package com.starfishst.bot.handlers.data.types.queues;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.types.GuidoMatch;
import com.starfishst.bot.handlers.data.types.GuidoTeam;
import com.starfishst.bot.handlers.data.types.GuidoTeamMember;
import com.starfishst.bot.handlers.data.types.maps.GuidoLinkedValuesMap;
import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.TeamMember;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
    if (this.getWaiting().size() >= ladder.playersPerTeam() * 2) {
      Set<TeamMember> participants = new HashSet<>();
      for (int i = 0; i < ladder.playersPerTeam() * 2; i++) {
        participants.add(new GuidoTeamMember(this.getWaiting().get(i), TeamRole.NORMAL));
      }
      this.getWaiting().removeAll(participants);
      return new GuidoMatch(
          this.getGuildId(),
          Lots.set(new GuidoTeam(participants, "participants")),
          new GuidoLinkedValuesMap("type", "pgm").addValue("ladder", ladder.getName()));
    }
    return null;
  }

  @Override
  public boolean join(@NotNull LinkedInfo info) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    LinkedData data = info.getLink();
    if (bungee != null && data != null) {
      Collection<LinkedData> links = data.getLinks(LinkedDataType.MINECRAFT);
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
                if (super.join(toPlay)) {
                  bungee.sendRequest(
                      new Request<>(Boolean.class, "add-queue", Maps.singleton("uuid", uuid)),
                      ignored -> {});
                  return true;
                }
              } else {
                data.sendMessage("You must join the googas minecraft server");
              }
              return false;
            }
          } catch (MessengerListenFailException e) {
            data.sendMessage("We could not check if you are connected to bungee");
          }
        } else {
          throw new IllegalStateException(
              "Some of the minecraft links of "
                  + links
                  + " is wrong as it does not have uuid or an invalid one!");
        }
      } else {
        data.sendMessage("You must link minecraft first");
      }
    }
    return false;
  }

  @Override
  public boolean leave(@NotNull LinkedInfo data) {
    if (super.leave(data)) {
      JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
      LinkedData linked = data.getLink();
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
