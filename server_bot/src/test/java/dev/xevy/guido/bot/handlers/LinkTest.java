package dev.xevy.guido.bot.handlers;

import dev.xevy.guido.bot.GuidoTestRuntime;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.user.UserData;
import me.googas.bot.core.handlers.link.LinkHandler;
import me.googas.net.sockets.json.client.JsonClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LinkTest {

  private GuidoTestRuntime runtime;
  private JsonClient client;

  @BeforeEach
  void setUp() throws IOException {
    runtime = GuidoTestRuntime.createRuntime();
    client = setupClient(runtime);
  }

  @NonNull
  private JsonClient setupClient(@NonNull GuidoTestRuntime runtime) throws IOException {
    return runtime.joinWithClient();
  }

  @Test
  @DisplayName("Linking must work for Minecraft")
  public void linkMinecraft() throws ExecutionException, InterruptedException {
    MinecraftLinkable foo = runtime.createMinecraftLinkable("Foo", false);
    UserData data = runtime.getLoader().getUsers().create();

    boolean isLinked = Requests.MinecraftLinks.isLinked(foo.getId()).future(client).get();
    Assertions.assertFalse(isLinked, "Minecraft linkable must not be associated with an account");
    String code = Requests.MinecraftLinks.linkNew(foo.getId()).future(client).get();
    MinecraftLinkable linkable =
        (MinecraftLinkable) runtime.getHandlers().getHandler(LinkHandler.class).getLinkable(code);
    Assertions.assertEquals(foo.getId(), linkable.getId());
    linkable.setLinkedUser(data);

    boolean isLinkedAfter = Requests.MinecraftLinks.isLinked(foo.getId()).future(client).get();
    Assertions.assertTrue(isLinkedAfter, "Minecraft linkable must be associated with an user now");
  }
}
