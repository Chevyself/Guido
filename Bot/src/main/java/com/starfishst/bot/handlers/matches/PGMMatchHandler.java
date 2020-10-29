package com.starfishst.bot.handlers.matches;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.match.MatchLoadedEvent;
import com.starfishst.bot.server.IGuidoServer;
import com.starfishst.bot.util.console.Console;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.Validate;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;

/** Handles matches for PGM */
public class PGMMatchHandler implements MatchHandler {

  /** All the matches that are looking for a server */
  @NotNull private final Set<BotMatch> waitingForServer = new HashSet<>();

  /**
   * Listen to a match being loaded to look for a server and start playing
   *
   * @param event the event of a match being loaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchLoaded(@NotNull MatchLoadedEvent event) {
    BotMatch match = event.getMatch();
    String type = match.getDetails().getValue("type", String.class);
    if (type != null && type.equalsIgnoreCase("pgm")) {
      if (match.getStatus() == MatchStatus.WAITING) {
        Console.debug(match + " is ready to look for a server");
        this.waitingForServer.add(match);
        this.lookForServer(match);
      }
    }
  }

  /**
   * Look for a server where the match can be played
   *
   * @param match the match looking for the server
   */
  public void lookForServer(@NotNull BotMatch match) {
    IGuidoServer server = Guido.getServer();
    JsonClientThread bungee = server.getAuthenticator().getBungee();
    Console.debug("Looking for a server to play " + match);
    if (bungee != null) {
      server.sendRequest(
          new Request<>(Boolean.class, "can-host", Maps.singleton("match", match)),
          ((messenger, canHost) -> {
            Console.debug("Can messenger host? " + canHost);
            if (this.waitingForServer.contains(match)) {
              if (canHost != null && canHost) {
                this.pleaseHost(match, bungee, messenger);
              }
            }
          }));
    } else {
      Console.debug("There's no connection with bungee");
    }
  }

  /**
   * Request a server to host a match
   *
   * @param match the match to host
   * @param bungee the instance of the bungee
   * @param messenger the server that is supposed to be able to host the match
   */
  public void pleaseHost(
      @NotNull BotMatch match, JsonClientThread bungee, JsonMessenger messenger) {
    Console.debug("Asking " + messenger + " to host " + match);
    messenger.sendRequest(
        new Request<>(String.class, "host", Maps.singleton("match", match)),
        serverIp -> {
          Console.debug(messenger + " has the ip " + serverIp);
          if (serverIp != null) {
            this.waitingForServer.remove(match);
            List<UUID> participants = new ArrayList<>();
            for (Team team : match.getTeams()) {
              for (TeamMember member : team.getMembers()) {
                String trimmed =
                    member.getLinkInfo().getIdentification().getValue("uuid", String.class);
                participants.add(
                    UUIDUtils.untrim(
                        Validate.notNull(trimmed, "Queueing user does not have uuid")));
              }
            }
            this.sendParticipantsToServer(bungee, serverIp, participants);
          }
        });
  }

  /**
   * Send all the participants to the server
   *
   * @param bungee the bungee server to send the participants
   * @param serverIp the ip of the server
   * @param participants list of uuid of the participants
   */
  public void sendParticipantsToServer(
      JsonClientThread bungee, String serverIp, List<UUID> participants) {
    Console.info("Sending participants to the server");
    bungee.sendRequest(
        new Request<>(
            Boolean.class,
            "send-to-server-by-ip",
            Maps.objects("uuids", participants).append("server", serverIp).build()),
        joined -> Console.debug("At least one of " + participants + " joined the server"));
  }

  @Override
  public void close() {}
}
