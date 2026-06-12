package me.googas.bot.core.handlers.matches;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.events.match.MinecraftMatchLoadedEvent;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.immutable.ImmutableMinecraftMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.bot.api.Guido;
import me.googas.net.api.Server;
import me.googas.net.sockets.json.JsonMessenger;
import me.googas.net.sockets.json.server.JsonClientThread;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import me.googas.starbox.logging.LoggerFactory;

/** Handles matches for PGM */
// TODO fix this handler and PGM queue
public class PGMMatchHandler implements MatchHandler {

  @NonNull private final Logger logger = LoggerFactory.getLogger(PGMMatchHandler.class);

  /** All the matches that are looking for a server */
  @NonNull private final Set<MinecraftMatch> waitingForServer = new HashSet<>();

  /**
   * Listen to a match being loaded to look for a server and start playing
   *
   * @param event the event of a match being loaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchLoaded(@NonNull MinecraftMatchLoadedEvent event) {
    MinecraftMatch abstractMatch = event.getMatch();
    if (abstractMatch.getStatus() == MatchStatus.WAITING) {
      this.waitingForServer.add(abstractMatch);
      this.lookForServer(abstractMatch);
    }
  }

  /**
   * Wait for a match to finish for other matches to look for servers
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdated(@NonNull MinecraftMatchStatusUpdatedEvent event) {
    if (event.getStatus() == MatchStatus.FINISHED) {
      this.lookForServers();
    }
  }

  /** Makes all the matches waiting for servers look for a server */
  public void lookForServers() {
    if (this.waitingForServer.isEmpty()) return;
    for (MinecraftMatch abstractMatch : this.waitingForServer) {
      this.lookForServer(abstractMatch);
    }
  }

  /**
   * Look for a server where the match can be played
   *
   * @param match the match looking for the server
   */
  public void lookForServer(@NonNull MinecraftMatch match) {
    if (match.getStatus() != MatchStatus.WAITING) {
      this.waitingForServer.remove(match);
      return;
    }
    Server<JsonClientThread> server = Guido.getServer();
    JsonClientThread bungee = Guido.getAuthenticator().getBungee().orElse(null);
    Requests.MatchServer.canHost(new ImmutableMinecraftMatch(match))
        .future(server)
        .whenComplete(
            (map, e) -> {
              if (e != null) {
                logger.log(Level.SEVERE, "Failed while looking for servers for hosting", e);
              }
              map.forEach(
                  (client, canHost) -> {
                    if (!canHost) return;
                    if (this.waitingForServer.remove(match)) {
                      this.pleaseHost(match, bungee, client);
                    }
                  });
            });
  }

  /**
   * Request a server to host a match
   *
   * @param match the match to host
   * @param bungee the instance of the bungee
   * @param messenger the server that is supposed to be able to host the match
   */
  public void pleaseHost(
      @NonNull MinecraftMatch match, JsonClientThread bungee, JsonMessenger messenger) {
    List<UUID> participants = new ArrayList<>();
    for (MinecraftMatchTeamMember info : match.getParticipants()) {
      participants.add(info.getId());
    }
    Requests.MatchServer.host(new ImmutableMinecraftMatch(match))
        .future(messenger)
        .whenComplete(
            (ip, e) -> {
              if (e != null) {
                logger.log(Level.SEVERE, "Failed to send host request to client " + messenger, e);
              }
              if (ip == null) return;
              if (bungee != null) {
                sendParticipantsToServer(match, bungee, ip, participants);
              }
              match.setServer(ip);
            });
  }

  private void sendParticipantsToServer(
      MinecraftMatch match, JsonClientThread bungee, String ip, List<UUID> participants) {
    this.sendParticipantsToServer(bungee, ip, participants);
    Requests.Bungee.serverName(ip)
        .send(bungee, optinal -> optinal.ifPresent(name -> match.setServer(name)));
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
