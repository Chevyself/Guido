package me.googas.bot.core.types.queues;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Queueable;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoMatch;
import me.googas.bot.core.types.GuidoTeam;
import me.googas.bot.core.types.GuidoTeamMember;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.commons.Lots;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.server.JsonClientThread;

/** A queue that uses pgm */
public class GuidoPGMQueue extends GuidoQueue {

  /**
   * Create the queue
   *
   * @param guildId the id of the guild where the queue is happening
   * @param ladder the names of the ladders waiting for queue
   */
  public GuidoPGMQueue(long guildId, @NonNull String ladder) {
    super(guildId, ladder);
  }

  @Override
  public GuidoMatch checkReady() {
    Ladder ladder = this.getLadder();
    if (this.getWaiting().size() >= ladder.playersPerTeam() * 2) {
      Set<TeamMember> participants = new HashSet<>();
      for (int i = 0; i < ladder.playersPerTeam() * 2; i++) {
        Queueable queueable = this.getWaiting().get(i);
        if (!(queueable instanceof LinkableInfo)) continue;
        participants.add(new GuidoTeamMember((LinkableInfo) queueable, TeamRole.NORMAL));
      }
      for (TeamMember participant : participants) {
        this.getWaiting().remove(participant.getLinkInfo());
      }
      return new GuidoMatch(
              this.getGuildId(),
              Lots.set(new GuidoTeam(-2, participants, "participants")),
              new GuidoLinkedValuesMap("type", "pgm").put("ladder", ladder.getName()))
          .cache();
    }
    return null;
  }

  @Override
  public boolean join(@NonNull LinkableInfo info) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    Linkable data = info.getLink();
    if (bungee != null && data != null) {
      Collection<Linkable> links = data.getLinks(LinkableType.MINECRAFT);
      if (!links.isEmpty()) {
        UUID uuid = null;
        LinkableInfo toPlay = null;
        for (Linkable link : links) {
          String trimmed = link.getIdentification().get("uuid", String.class);
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
  public boolean leave(@NonNull LinkableInfo data) {
    if (super.leave(data)) {
      JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
      Linkable linked = data.getLink();
      if (bungee != null && linked != null) {
        String trimmed = linked.getIdentification().get("uuid", String.class);
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
