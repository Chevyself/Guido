package dev.xevy.bukkit;

import com.google.gson.Gson;
import java.io.IOException;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.client.Client;
import me.googas.net.api.Messenger;
import me.googas.net.sockets.json.JsonReceptor;
import me.googas.net.sockets.json.ReceivedJsonRequest;
import me.googas.net.sockets.json.exception.JsonExternalCommunicationException;
import me.googas.net.sockets.json.exception.JsonInternalCommunicationException;
import me.googas.net.sockets.json.server.JsonSocketServer;
import org.junit.jupiter.api.Test;

public class BukkitClientTest {
  @Test
  public void test() throws IOException, InterruptedException {
    int port = 3000;
    JsonSocketServer server =
        JsonSocketServer.listen(port)
            .addReceptors(
                new JsonReceptor() {
                  @Override
                  public Object execute(
                      Messenger messenger,
                      @NonNull ReceivedJsonRequest receivedJsonRequest,
                      @NonNull Gson gson)
                      throws JsonExternalCommunicationException,
                          JsonInternalCommunicationException {
                    return true;
                  }

                  @Override
                  public @NonNull String getRequestMethod() {
                    return Requests.Server.AUTH;
                  }
                })
            .start();
    Client bukkitClient = new Client("none", "localhost", port, 1000);
    bukkitClient
        .startConnection()
        .thenAccept(
            bukkitAuth -> {
              System.out.println(bukkitAuth);
            });
  }
}
