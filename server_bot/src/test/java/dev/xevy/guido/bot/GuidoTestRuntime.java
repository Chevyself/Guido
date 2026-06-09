package dev.xevy.guido.bot;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.user.UserData;
import me.googas.bot.GuidoBot;
import me.googas.bot.GuidoBotConfig;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.net.api.Messenger;
import me.googas.net.sockets.json.JsonReceptor;
import me.googas.net.sockets.json.ReceivedJsonRequest;
import me.googas.net.sockets.json.client.JsonClient;

public final class GuidoTestRuntime implements GuidoBotRuntime {

  @NonNull public static final String LOCALHOST = "localhost";
  @NonNull @Delegate private final GuidoBot parent;
  @NonNull @Getter private final GuidoBotConfig config;

  private GuidoTestRuntime(@NonNull GuidoBot parent, @NonNull GuidoBotConfig config) {
    this.parent = parent;
    this.config = config;
  }

  @NonNull
  public static GuidoTestRuntime createRuntime() {
    GuidoBotConfig config = new JGuidoConfig();
    GuidoBot bot = new GuidoBot(new JGuidoRuntime(), config);
    bot.start();
    return new GuidoTestRuntime(bot, config);
  }

  @NonNull
  public MinecraftLinkable createMinecraftLinkable(@NonNull String nickname, boolean link) {
    MinecraftLinkable linkable =
        this.getLoader().getMinecraftLinks().updateOrCreate(UUID.randomUUID(), nickname, "", true);
    if (link) {
      UserData userData = this.getLoader().getUsers().create();
      linkable.setLinkedUser(userData);
    }
    return linkable;
  }

  @NonNull
  public JsonClient joinWithClient() throws IOException {
    return JsonClient.join(GuidoTestRuntime.LOCALHOST, this.config.getServerPort()).start();
  }

  @NonNull
  public JsonClient joinWithClient(@NonNull JsonReceptor... receptors) throws IOException {
    return JsonClient.join(GuidoTestRuntime.LOCALHOST, this.config.getServerPort())
        .maxWait(60000)
        .addReceptors(receptors)
        .handle(Throwable::printStackTrace)
        .start();
  }

  @NonNull
  public JsonReceptor listen(
      @NonNull String method, @NonNull JsonReceptorHandleFunction<Object> consumer) {
    return new JsonReceptor() {
      @Override
      public Object execute(
          Messenger messenger,
          @NonNull ReceivedJsonRequest receivedJsonRequest,
          @NonNull Gson gson) {
        try {
          return consumer.apply(new JClientContext(messenger, receivedJsonRequest, gson));
        } catch (ExecutionException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public @NonNull String getRequestMethod() {
        return method;
      }
    };
  }
}
