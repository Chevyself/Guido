package me.googas.bot.core.handlers.matches;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.bot.api.events.match.MatchLoadedEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.Guido;
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
  @NotNull private final Set<Match> waitingForServer = new HashSet<>();

  /**
   * Listen to a match being loaded to look for a server and start playing
   *
   * @param event the event of a match being loaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchLoaded(@NotNull MatchLoadedEvent event) {
    Match match = event.getMatch();
    String type = match.getDetails().get("type", String.class);
    if (type != null && type.equalsIgnoreCase("pgm")) {
      if (match.getStatus() == MatchStatus.WAITING) {
        this.waitingForServer.add(match);
        this.lookForServer(match);
      }
    }
  }

  /**
   * Wait for a match to finish for other matches to look for servers
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdated(@NotNull MatchStatusUpdatedEvent event) {
    if (event.getStatus() == MatchStatus.FINISHED
        && "pgm".equalsIgnoreCase(event.getMatch().getDetails().get("type", String.class))) {
      this.lookForServers();
    }
  }

  /** Makes all the matches waiting for servers look for a server */
  public void lookForServers() {
    for (Match match : this.waitingForServer) {
      this.lookForServer(match);
    }
  }

  /**
   * Look for a server where the match can be played
   *
   * @param match the match looking for the server
   */
  public void lookForServer(@NotNull Match match) {
    BotServer server = Guido.getServer();
    JsonClientThread bungee = server.getAuthenticator().getBungee();
    if (bungee != null) {
      server.sendRequest(
          new Request<>(Boolean.class, "can-host", Maps.singleton("match", match)),
          ((messenger, canHost) -> {
            if (this.waitingForServer.contains(match) && canHost != null && canHost) {
              this.waitingForServer.remove(match);
              this.pleaseHost(match, bungee, messenger);
            }
          }));
    } else {
    }
  }

  /**
   * Request a server to host a match
   *
   * @param match the match to host
   * @param bungee the instance of the bungee
   * @param messenger the server that is supposed to be able to host the match
   */
  public void pleaseHost(@NotNull Match match, JsonClientThread bungee, JsonMessenger messenger) {
    List<UUID> participants = new ArrayList<>();
    for (LinkableInfo info : match.getParticipants()) {
      String trimmed = info.getIdentification().get("uuid", String.class);
      participants.add(
          UUIDUtils.untrim(Validate.notNull(trimmed, "Queueing user does not have uuid")));
    }
    messenger.sendRequest(
        new Request<>(String.class, "host", Maps.singleton("match", match)),
        serverIp -> {
          if (serverIp != null) {
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
    bungee.sendRequest(
        new Request<>(
            Boolean.class,
            "send-to-server-by-ip",
            Maps.objects("uuids", participants).append("server", serverIp).build()),
        joined -> {});
  }

  @Override
  public void close() {}
}
