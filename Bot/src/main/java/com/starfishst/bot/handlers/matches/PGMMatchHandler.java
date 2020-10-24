package com.starfishst.bot.handlers.matches;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.match.MatchLoadedEvent;
import com.starfishst.bot.server.IGuidoServer;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.MatchStatus;
import com.starfishst.guido.api.data.matches.Team;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.Validate;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;

/** Handles matches for PGM */
public class PGMMatchHandler implements MatchHandler {

  @NotNull private final Set<BotMatch> waitingForServer = new HashSet<>();

  @Listener
  public void onMatchLoaded(@NotNull MatchLoadedEvent event) {
    BotMatch match = event.getMatch();
    String type = match.getDetails().getValue("type", String.class);
    if (type != null && type.equalsIgnoreCase("pgm")) {
      if (match.getStatus() == MatchStatus.READY) {
        // LOOK FOR SERVER
        this.waitingForServer.add(match);
        IGuidoServer server = Guido.getServer();
        JsonClientThread bungee = server.getAuthenticator().getBungee();
        if (bungee != null) {
          server.sendRequest(
              new Request<>(Boolean.class, "can-host", Maps.singleton("match", match.getId())),
              ((messenger, bol) -> {
                if (this.waitingForServer.contains(match)) {
                  if (bol) {
                    messenger.sendRequest(
                        new Request<>(String.class, "host", Maps.singleton("match", match.getId())),
                        serverIp -> {
                          if (serverIp != null) {
                            this.waitingForServer.remove(match);
                            List<UUID> participants = new ArrayList<>();
                            for (Team team : match.getTeams()) {
                              for (LinkedInfo info : team.getMembers().keySet()) {
                                String trimmed =
                                    info.getIdentification().getValue("uuid", String.class);
                                participants.add(
                                    UUIDUtils.untrim(
                                        Validate.notNull(
                                            trimmed, "Queueing user does not have uuid")));
                              }
                            }
                            bungee.sendRequest(
                                new Request<>(
                                    Boolean.class,
                                    "send-to-server-by-ip",
                                    Maps.objects("uuids", participants)
                                        .append("server", serverIp)
                                        .build()),
                                joined -> {
                                  // IGNORED
                                });
                          }
                        });
                  }
                }
              }));
        }
      }
    }
  }

  @Override
  public void close() {}
}
