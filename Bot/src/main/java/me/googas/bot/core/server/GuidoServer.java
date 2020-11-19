package me.googas.bot.core.server;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.logging.Level;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.permissions.Permission;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.adapters.LinkedInfoAdapter;
import me.googas.bot.adapters.LinkedValuesMapAdapter;
import me.googas.bot.adapters.PermissionAdapter;
import me.googas.bot.adapters.TeamAdapter;
import me.googas.bot.adapters.TeamMemberAdapter;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.api.events.server.GuidoServerConnectionEvent;
import me.googas.bot.api.events.server.GuidoServerDisconnectionEvent;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.Guido;
import me.googas.bot.core.server.receptors.GroupReceptors;
import me.googas.bot.core.server.receptors.LadderReceptors;
import me.googas.bot.core.server.receptors.LinkReceptors;
import me.googas.bot.core.server.receptors.LinkedDataReceptors;
import me.googas.bot.core.server.receptors.MatchReceptors;
import me.googas.bot.core.server.receptors.MinecraftDataReceptors;
import me.googas.bot.core.server.receptors.QueueReceptors;
import me.googas.bot.core.server.receptors.SecurityReceptors;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.server.JsonClientThread;
import me.googas.messaging.json.server.JsonSocketServer;
import org.jetbrains.annotations.NotNull;

/** A server for implementations connections */
public class GuidoServer extends JsonSocketServer implements BotServer {

  /** The authentication system for the guido server */
  @NotNull private final GuidoAuthenticator authenticator = new GuidoAuthenticator();

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
            .registerTypeAdapter(Team.class, new TeamAdapter())
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
        this.authenticator);
  }

  @Override
  protected void onRemove(@NotNull JsonClientThread client) {
    this.authenticator.remove(client);
    new GuidoServerDisconnectionEvent(this, client).call();
  }

  @Override
  protected void onConnection(@NotNull JsonClientThread client) {
    this.authenticator.add(client);
    new GuidoServerConnectionEvent(this, client).call();
  }

  @Override
  public void close() throws IOException {
    this.sendRequest(new Request<>(Boolean.class, "disconnected"), (client, disconnected) -> {});
    super.close();
  }

  @NotNull
  @Override
  public GuidoAuthenticator getAuthenticator() {
    return this.authenticator;
  }
}
