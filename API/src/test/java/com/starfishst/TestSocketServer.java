package com.starfishst;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.json.JsonClientThread;
import com.starfishst.guido.api.implementations.messaging.json.JsonSocketServer;
import com.starfishst.guido.api.implementations.messaging.json.requests.Ping;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/** A test for socket servers */
public class TestSocketServer {

  public static void main(String[] args) throws IOException {
    JsonSocketServer server = new JsonSocketServer(3000, 1000);
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                for (JsonClientThread client : server.getClients()) {
                  client.sendRequest(
                      new Request("ping", Maps.singleton("start", System.currentTimeMillis())),
                      Ping.class,
                      ping -> {
                        System.out.println(ping.getMillis());
                      });
                }
              }
            },
            1000,
            3000);
    server.start();
  }
}
