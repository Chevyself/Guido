package me.googas.bot.core.handlers.matches;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.events.match.MatchLoadedEvent;
import me.googas.api.events.match.MatchStatusUpdatedEvent;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.bot.api.Guido;
import me.googas.bot.api.server.BotServer;
import me.googas.commons.UUIDUtils;
import me.googas.commons.Validate;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.server.JsonClientThread;

/** Handles matches for PGM */
// TODO fix this handler and PGM queue
public class PGMMatchHandler implements MatchHandler {

  /** All the matches that are looking for a server */
  @NonNull private final Set<AbstractMatch> waitingForServer = new HashSet<>();

  /**
   * Listen to a match being loaded to look for a server and start playing
   *
   * @param event the event of a match being loaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchLoaded(@NonNull MatchLoadedEvent event) {
    AbstractMatch abstractMatch = event.getAbstractMatch();
    String type = abstractMatch.getString(null, "type", "");
    if (type != null && type.equalsIgnoreCase("pgm")) {
      if (abstractMatch.getStatus() == MatchStatus.WAITING) {
        this.waitingForServer.add(abstractMatch);
        this.lookForServer(abstractMatch);
      }
    }
  }

  /**
   * Wait for a match to finish for other matches to look for servers
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdated(@NonNull MatchStatusUpdatedEvent event) {
    if (event.getStatus() == MatchStatus.FINISHED
        && "pgm".equalsIgnoreCase(event.getAbstractMatch().getString(null, "type", ""))) {
      this.lookForServers();
    }
  }

  /** Makes all the matches waiting for servers look for a server */
  public void lookForServers() {
    if (this.waitingForServer.isEmpty()) return;
    for (AbstractMatch abstractMatch : this.waitingForServer) {
      this.lookForServer(abstractMatch);
    }
  }

  /**
   * Look for a server where the abstractMatch can be played
   *
   * @param abstractMatch the abstractMatch looking for the server
   */
  public void lookForServer(@NonNull AbstractMatch abstractMatch) {
    if (abstractMatch.getStatus() != MatchStatus.WAITING) {
      this.waitingForServer.remove(abstractMatch);
      return;
    }
    BotServer server = Guido.getServer();
    JsonClientThread bungee = server.getAuthenticator().getBungee();
    if (bungee != null) {
      Requests.MatchServer.canHost(abstractMatch)
          .send(
              server,
              (messenger, canHost) -> {
                if (!(messenger instanceof JsonClientThread)) return;
                if (!canHost.isPresent()) return;
                if (this.waitingForServer.contains(abstractMatch) && canHost.get()) {
                  this.waitingForServer.remove(abstractMatch);
                  this.pleaseHost(abstractMatch, bungee, (JsonMessenger) messenger);
                }
              });
    }
  }

  /**
   * Request a server to host a abstractMatch
   *
   * @param abstractMatch the abstractMatch to host
   * @param bungee the instance of the bungee
   * @param messenger the server that is supposed to be able to host the abstractMatch
   */
  public void pleaseHost(
      @NonNull AbstractMatch abstractMatch, JsonClientThread bungee, JsonMessenger messenger) {
    List<UUID> participants = new ArrayList<>();
    for (LinkableInfo info : abstractMatch.getParticipants()) {
      String trimmed = info.getIdString("uuid", "");
      participants.add(
          UUIDUtils.untrim(Validate.notNull(trimmed, "Queueing user does not have uuid")));
    }
    Requests.MatchServer.host(abstractMatch)
        .send(
            messenger,
            Requests.ifPresentElse(
                ip -> {
                  this.sendParticipantsToServer(bungee, ip, participants);
                  Requests.Bungee.serverName(ip)
                      .send(
                          bungee,
                          optinal ->
                              optinal.ifPresent(name -> abstractMatch.set(null, "server", name)));
                },
                () -> this.waitingForServer.add(abstractMatch)));
  }

  /**
   * Send all the participants to the server
   *
   * @param bungee the bungee server to send the participants
   * @param serverIp the ip of the server
   * @param participants list of uuid of the participants
   */
  public void sendParticipantsToServer(
      @NonNull JsonClientThread bungee,
      @NonNull String serverIp,
      @NonNull List<UUID> participants) {
    Requests.Bungee.sendToServerByIp(participants, serverIp).queue(bungee);
  }

  /** Called when a server is ready to host a match */
  @Override
  public void serverReady() {
    this.lookForServers();
  }

  @Override
  public void onDisable() {}
}
