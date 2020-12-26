package me.googas.bot.core.server;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.logging.Level;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.permissions.Permission;
import me.googas.bot.Guido;
import me.googas.bot.adapters.LinkedValuesMapAdapter;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.adapters.links.LinkedInfoAdapter;
import me.googas.bot.adapters.matches.team.MatchTeamAdapter;
import me.googas.bot.adapters.matches.team.TeamMemberAdapter;
import me.googas.bot.adapters.permissions.PermissionAdapter;
import me.googas.bot.api.events.server.GuidoServerConnectionEvent;
import me.googas.bot.api.events.server.GuidoServerDisconnectionEvent;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.server.receptors.GroupReceptors;
import me.googas.bot.core.server.receptors.LadderReceptors;
import me.googas.bot.core.server.receptors.LinkReceptors;
import me.googas.bot.core.server.receptors.LinkedDataReceptors;
import me.googas.bot.core.server.receptors.MatchReceptors;
import me.googas.bot.core.server.receptors.MinecraftDataReceptors;
import me.googas.bot.core.server.receptors.QueueReceptors;
import me.googas.bot.core.server.receptors.SecurityReceptors;
import me.googas.bot.core.server.receptors.TeamDataReceptors;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.server.JsonClientThread;
import me.googas.messaging.json.server.JsonSocketServer;

/** A server for implementations connections */
public class GuidoServer extends JsonSocketServer implements BotServer {

  /** The authentication system for the guido server */
  @NonNull private final GuidoAuthenticator authenticator = new GuidoAuthenticator();

  /**
   * Creates the guido socket server
   *
   * @param port the port to which the server will be listening to
   * @param timeout the time too timeout requests
   * @throws IOException if the port is already in use
   */
  public GuidoServer(int port, long timeout) throws IOException {
    super(
        port,
        throwable ->
            Guido.getLogger()
                .log(Level.SEVERE, throwable, () -> "An exception was cached in the socket server"),
        null,
        new GsonBuilder()
            // Required by Commons-Communication
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            // For custom receptors
            .registerTypeAdapter(LinkableInfo.class, new LinkedInfoAdapter())
            .registerTypeAdapter(GuidoLinkedValuesMap.class, new LinkedValuesMapAdapter())
            .registerTypeAdapter(Permission.class, new PermissionAdapter())
            .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(GuidoValuesMap.class, new ValuesMapAdapter())
            .registerTypeAdapter(MatchTeam.class, new MatchTeamAdapter())
            .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
            .setPrettyPrinting()
            .create(),
        timeout);
    this.setAuthenticator(this.authenticator);
    this.addReceptors(
        new GroupReceptors(),
        new LadderReceptors(),
        new LinkedDataReceptors(),
        new LinkReceptors(),
        new MatchReceptors(),
        new MinecraftDataReceptors(),
        new QueueReceptors(),
        new SecurityReceptors(),
        new TeamDataReceptors(),
        this.authenticator);
  }

  @Override
  protected void onRemove(@NonNull JsonClientThread client) {
    this.authenticator.remove(client);
    new GuidoServerDisconnectionEvent(this, client).call();
  }

  @Override
  protected void onConnection(@NonNull JsonClientThread client) {
    this.authenticator.add(client);
    new GuidoServerConnectionEvent(this, client).call();
  }

  @Override
  public void close() throws IOException {
    this.sendRequest(new Request<>(Boolean.class, "disconnected"), (client, disconnected) -> {});
    super.close();
  }

  @NonNull
  @Override
  public GuidoAuthenticator getAuthenticator() {
    return this.authenticator;
  }
}
